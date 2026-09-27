package com.example.scheduler.execution;

import com.example.scheduler.SchedulerApplication;
import com.example.scheduler.history.application.HistoryExecutionCoordinator;
import com.example.scheduler.history.domain.*;
import com.example.scheduler.history.infra.persistent.JobExecutionHistoryJpaRepository;
import com.example.scheduler.lease.domain.StaleExecutorException;
import com.example.scheduler.reconciliation.application.ReconciliationService;
import com.example.scheduler.reconciliation.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = SchedulerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.config.location=classpath:execution-test.yml")
class ReconciliationIntegrationTest {
    @Autowired HistoryExecutionCoordinator coordinator;
    @Autowired ReconciliationRepository repository;
    @Autowired ReconciliationService service;
    @Autowired JobExecutionHistoryJpaRepository histories;
    @Autowired JdbcTemplate jdbc;

    @Test void unknownToSuccessPreservesAttemptLeaseAndFencing() {
        String id = scheduled();
        var claim = coordinator.claim(id).orElseThrow();
        coordinator.finish(claim, null, "lost acknowledgement");
        var attempt = jdbc.queryForMap("select * from CHECK_BATCH_ATTEMPT where EXECUTION_ID=?", id);
        var lease = jdbc.queryForMap("select * from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID=?", id);
        var result = reconcile(id, new ReconciliationEvidence(true, false, false, "Committed business receipt for " + id));
        assertThat(result.status()).isEqualTo(ExecutionStatus.SUCCESS);
        assertThat(result.changed()).isTrue();
        assertThat(result.reason()).contains("Committed business receipt");
        assertThat(result.reconciledAt()).isNotNull();
        assertThat(jdbc.queryForMap("select * from CHECK_BATCH_ATTEMPT where EXECUTION_ID=?", id)).isEqualTo(attempt);
        assertThat(jdbc.queryForMap("select * from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID=?", id)).isEqualTo(lease);
        assertThatThrownBy(() -> coordinator.finish(claim, 0, null)).isInstanceOf(StaleExecutorException.class);
        assertThatThrownBy(() -> coordinator.heartbeat(claim)).isInstanceOf(StaleExecutorException.class);
        assertThat(histories.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    }

    @Test void unknownToRetryWaitDoesNotRunRetry() {
        String id = unknown();
        assertThat(reconcile(id, new ReconciliationEvidence(false, true, true,
                "Worker termination confirmed; authoritative launch journal proves no business execution")).status())
                .isEqualTo(ExecutionStatus.RETRY_WAIT);
        assertThat(coordinator.claim(id)).isEmpty();
        assertThat(coordinator.attempts(id)).hasSize(1);
        assertThat(coordinator.attempts(id).getFirst().getStatus().name()).isEqualTo("UNKNOWN");
    }

    @Test void unknownToManualReviewWithoutConfiguredVerifier() {
        String id = unknown();
        assertThat(service.findUnknown(500)).contains(id);
        var result = service.reconcile(id).orElseThrow();
        assertThat(result.status()).isEqualTo(ExecutionStatus.MANUAL_REVIEW);
        assertThat(result.reason()).contains("operator review required");
        assertThat(service.findResult(id).orElseThrow().reason()).isEqualTo(result.reason());
        assertThat(service.findUnknown(500)).doesNotContain(id);
        assertThat(coordinator.claim(id)).isEmpty();
    }

    @Test void incompleteOrContradictoryEvidenceRequiresReview() {
        for (var evidence : List.of(
                new ReconciliationEvidence(false, true, false, "Process is gone only"),
                new ReconciliationEvidence(false, false, true, "Termination not confirmed"),
                new ReconciliationEvidence(true, true, true, "Conflicting receipts"))) {
            assertThat(reconcile(unknown(), evidence).status()).isEqualTo(ExecutionStatus.MANUAL_REVIEW);
        }
    }

    @Test void successFailureAndOtherNonUnknownStatesAreNotTargets() {
        for (Integer exit : List.of(0, 1)) {
            String id = scheduled();
            coordinator.finish(coordinator.claim(id).orElseThrow(), exit, null);
            assertNotTarget(id);
        }
        String scheduled = scheduled();
        assertNotTarget(scheduled);
        coordinator.claim(scheduled).orElseThrow();
        assertNotTarget(scheduled);
    }

    private void assertNotTarget(String id) {
        long version = histories.findById(id).orElseThrow().getVersion();
        assertThat(repository.reconcile(id, (h, a) -> { throw new AssertionError("Verifier must not run"); })
                .orElseThrow().changed()).isFalse();
        assertThat(histories.findById(id).orElseThrow().getVersion()).isEqualTo(version);
        assertThat(auditCount(id)).isZero();
    }

    @Test void concurrentReconciliationVerifiesAndChangesOnlyOnce() throws Exception {
        String id = unknown();
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var secondStarted = new CountDownLatch(1);
        var calls = new AtomicInteger();
        ReconciliationVerifier verifier = (h, a) -> {
            calls.incrementAndGet();
            entered.countDown();
            try {
                if (!release.await(3, TimeUnit.SECONDS)) throw new IllegalStateException("Timed out");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return new ReconciliationEvidence(true, false, false, "Durable receipt");
        };
        try (var pool = Executors.newFixedThreadPool(2)) {
            var first = pool.submit(() -> repository.reconcile(id, verifier).orElseThrow());
            try {
                assertThat(entered.await(3, TimeUnit.SECONDS)).isTrue();
                var second = pool.submit(() -> {
                    secondStarted.countDown();
                    return repository.reconcile(id, verifier).orElseThrow();
                });
                assertThat(secondStarted.await(3, TimeUnit.SECONDS)).isTrue();
                assertThatThrownBy(() -> second.get(200, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);
                release.countDown();
                assertThat(first.get(5, TimeUnit.SECONDS).changed()).isTrue();
                assertThat(second.get(5, TimeUnit.SECONDS).changed()).isFalse();
            } finally {
                release.countDown();
            }
        }
        assertThat(calls.get()).isEqualTo(1);
        assertThat(auditCount(id)).isEqualTo(1);
    }

    @Test void databaseWriteFailureRollsBackStatusAndAudit() {
        String id = unknown();
        var before = histories.findById(id).orElseThrow();
        // Force a real DB constraint failure during flush, after the in-memory status change.
        jdbc.execute("alter table CHECK_BATCH_RECONCILIATION add constraint TEST_RECONCILIATION_FAIL "
                + "check (EXECUTION_ID <> '" + id + "')");
        try {
            assertThatThrownBy(() -> reconcile(id, new ReconciliationEvidence(true, false, false, "Receipt")))
                    .isInstanceOf(RuntimeException.class);
            var after = histories.findById(id).orElseThrow();
            assertThat(after.getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
            assertThat(after.getVersion()).isEqualTo(before.getVersion());
            assertThat(after.getMessage()).isEqualTo(before.getMessage());
            assertThat(auditCount(id)).isZero();
        } finally {
            jdbc.execute("alter table CHECK_BATCH_RECONCILIATION drop constraint TEST_RECONCILIATION_FAIL");
        }
        assertThat(reconcile(id, new ReconciliationEvidence(true, false, false, "Receipt")).changed()).isTrue();
    }

    @Test void repeatedCallsPreserveAllDecisionsAndOriginalReasons() {
        for (var evidence : List.of(
                new ReconciliationEvidence(true, false, false, "Receipt"),
                new ReconciliationEvidence(false, true, true, "Not launched and terminated"),
                new ReconciliationEvidence(false, false, false, "Insufficient evidence"))) {
            String id = unknown();
            var first = reconcile(id, evidence);
            long version = histories.findById(id).orElseThrow().getVersion();
            for (int i = 0; i < 3; i++) {
                var again = repository.reconcile(id, (h, a) -> { throw new AssertionError("Must not reverify"); }).orElseThrow();
                assertThat(again.changed()).isFalse();
                assertThat(again.status()).isEqualTo(first.status());
                assertThat(again.reason()).isEqualTo(first.reason());
                assertThat(again.reconciledAt()).isEqualTo(first.reconciledAt());
            }
            assertThat(histories.findById(id).orElseThrow().getVersion()).isEqualTo(version);
            assertThat(auditCount(id)).isEqualTo(1);
        }
    }

    @Test void verifierDatabaseErrorPreservesUnknown() {
        String id = unknown();
        assertThatThrownBy(() -> repository.reconcile(id, (h, a) -> {
            jdbc.queryForObject("select missing_column from CHECK_BATCH_RECONCILIATION", String.class);
            return new ReconciliationEvidence(true, false, false, "unreachable");
        })).isInstanceOf(RuntimeException.class);
        assertThat(histories.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
        assertThat(auditCount(id)).isZero();
    }

    @Test void expiredLeaseCanBeReconciledWithoutChangingItsFencingToken() {
        String id = scheduled();
        var claim = coordinator.claim(id).orElseThrow();
        jdbc.update("update CHECK_BATCH_EXECUTION_LEASE set EXPIRES_AT=? where EXECUTION_ID=?",
                java.sql.Timestamp.from(Instant.EPOCH), id);
        coordinator.expireLeases();
        var lease = jdbc.queryForMap("select * from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID=?", id);
        assertThat(reconcile(id, new ReconciliationEvidence(true, false, false, "Business receipt")).status())
                .isEqualTo(ExecutionStatus.SUCCESS);
        assertThat(jdbc.queryForMap("select * from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID=?", id)).isEqualTo(lease);
        assertThatThrownBy(() -> coordinator.finish(claim, 0, null)).isInstanceOf(StaleExecutorException.class);
        assertThat(coordinator.attempts(id).getFirst().getStatus().name()).isEqualTo("UNKNOWN");
    }

    private ReconciliationResult reconcile(String id, ReconciliationEvidence evidence) {
        return repository.reconcile(id, (h, a) -> evidence).orElseThrow();
    }
    private int auditCount(String id) {
        return jdbc.queryForObject("select count(*) from CHECK_BATCH_RECONCILIATION where EXECUTION_ID=?", Integer.class, id);
    }
    private String unknown() {
        String id = scheduled();
        coordinator.finish(coordinator.claim(id).orElseThrow(), null, "Outcome unknown");
        return id;
    }
    private String scheduled() {
        return coordinator.ensureExecution(new HistoryExecutionRequest("test", "reconciliation", UUID.randomUUID().toString(),
                "manual:test", Instant.now(), null, "MANUAL", "SHELL", "MANUAL", "echo test", null));
    }
}
