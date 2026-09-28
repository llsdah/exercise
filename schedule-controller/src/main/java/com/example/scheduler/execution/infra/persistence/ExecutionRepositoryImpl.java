package com.example.scheduler.execution.infra.persistence;

import com.example.scheduler.dependency.domain.ExecutionDependencies;
import com.example.scheduler.execution.application.port.ExecutionRepository;

import com.example.scheduler.execution.domain.*;
import com.example.scheduler.attempt.domain.*;
import com.example.scheduler.attempt.infra.persistence.BatchAttemptEntity;
import com.example.scheduler.lease.domain.*;
import com.example.scheduler.lease.infra.persistence.ExecutionLeaseEntity;
import com.example.scheduler.node.application.NodeRegistry;
import com.example.scheduler.retry.domain.RetryAdmission;
import com.example.scheduler.retry.domain.RetryCandidate;
import com.example.scheduler.global.infra.DatabaseClock;
import jakarta.persistence.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.*;
import java.util.*;

/** History is the sole logical execution ledger. Lock it before changing an attempt or lease. */
@Repository
public class ExecutionRepositoryImpl implements ExecutionRepository {
    @PersistenceContext private EntityManager em;
    private final JobExecutionHistoryJpaRepository histories;
    private final TransactionTemplate transaction;
    private final DatabaseClock clock;
    private final NodeRegistry nodes;
    private final ExecutionDependencies dependencies;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.scheduler.metrics.BatchMetrics batchMetrics;

    public ExecutionRepositoryImpl(JobExecutionHistoryJpaRepository histories, PlatformTransactionManager manager, DatabaseClock clock, ExecutionDependencies dependencies, NodeRegistry nodes) {
        this.histories = histories;
        this.clock = clock;
        this.nodes = nodes;
        this.dependencies = dependencies;
        transaction = new TransactionTemplate(manager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // Keep the existing transaction isolation and History lock for cross-node admission.
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transaction.setTimeout(5);
    }

    private Optional<JobExecutionHistoryEntity> occurrence(ExecutionRequest r) {
        return histories.findByTenantIdAndScheduleGroupAndScheduleNameAndOccurrenceKey(
                r.tenantId(), r.scheduleGroup(), r.scheduleName(), r.occurrenceKey());
    }

    @Override
    public String ensureExecution(ExecutionRequest r) {
        var existing = occurrence(r);
        if (existing.isPresent()) return existing.get().getExecutionId();
        try {
            return transaction.execute(tx -> {
                var history = JobExecutionHistoryEntity.builder()
                        .executionId(UUID.randomUUID().toString())
                        .tenantId(r.tenantId())
                        .scheduleGroup(r.scheduleGroup())
                        .scheduleName(r.scheduleName())
                        .occurrenceKey(r.occurrenceKey())
                        .scheduledAt(r.scheduledAt())
                        .fireInstanceId(r.fireInstanceId())
                        .triggerNodeId(r.triggerNodeId())
                        .scheduleType(r.scheduleType())
                        .jobType(r.jobType())
                        .cronExpression(r.cronExpression() == null || r.cronExpression().isBlank() ? "MANUAL" : r.cronExpression())
                        .command(r.command())
                        .parameters(r.parameters())
                        .status(ExecutionStatus.SCHEDULED)
                        .executionCount(0L).build();
                dependencies.capture(history.toDomain());
                em.persist(history);
                em.persist(ExecutionLeaseEntity.unowned(history.getExecutionId()));
                histories.flush();
                return history.getExecutionId();
            });
        } catch (DataIntegrityViolationException conflict) {
            // Re-read after the failed insertion transaction has rolled back.
            return occurrence(r).map(JobExecutionHistoryEntity::getExecutionId).orElseThrow(() -> conflict);
        }
    }

    @Override
    public Optional<LeaseClaim> claim(String id, String node, Duration ttl) {
        return transaction.execute(tx -> {
            boolean active = nodes.lockActive(node);
            var history = locked(id);
            if (!active) {
                if (history.getStatus() == ExecutionStatus.SCHEDULED) history.setStatus(ExecutionStatus.WAITING);
                return Optional.empty();
            }
            var lease = em.find(ExecutionLeaseEntity.class, id);
            Instant now = databaseNow(id);
            if (history.getStatus().hasProcessOwner() && !lease.getExpiresAt().isAfter(now)) expire(history, lease, now);
            if (!history.getStatus().canAcquireLease()) return Optional.empty();
            return Optional.ofNullable(acquire(history, lease, node, ttl, now));
        });
    }

    private LeaseClaim acquire(JobExecutionHistoryEntity history, ExecutionLeaseEntity lease, String node, Duration ttl, Instant now) {
        var dependency = dependencies.evaluate(history.toDomain());
        if (!dependency.ready()) {
            history.setStatus(dependency.manualReview() ? ExecutionStatus.MANUAL_REVIEW : ExecutionStatus.WAITING);
            history.setMessage(dependency.reason());
            return null;
        }
        lease.acquire(node, now.plus(ttl));
        history.setStatus(ExecutionStatus.RUNNING);
        history.setAttemptCount(history.getAttemptCount() + 1);
        history.setExecutionCount((long) history.getAttemptCount());
        if (history.getStartTime() == null) history.setStartTime(local(now));
        history.setEndTime(null);
        history.setDuration(null);
        history.setRetryWaitSince(null);
        var attempt = BatchAttemptEntity.starting(history.getExecutionId(), history.getAttemptCount(), node, lease.getLeaseToken(), now);
        em.persist(attempt);
        return new LeaseClaim(history.getExecutionId(), attempt.getId(), node, lease.getLeaseToken());
    }

    @Override
    public List<RetryCandidate> findRetryCandidates(int limit, Duration backoff) {
        if (limit < 1 || limit > 500 || backoff.isNegative()) throw new IllegalArgumentException("Invalid retry scan");
        return transaction.execute(tx -> {
            var times = em.createQuery("select current_timestamp from JobExecutionHistoryEntity h", java.sql.Timestamp.class)
                    .setMaxResults(1).getResultList();
            if (times.isEmpty()) return List.of();
            return em.createQuery("select h from JobExecutionHistoryEntity h where h.status = :state "
                            + "and (h.retryWaitSince is null or h.retryWaitSince <= :cutoff) order by h.retryWaitSince, h.executionId",
                            JobExecutionHistoryEntity.class)
                    .setParameter("state", ExecutionStatus.RETRY_WAIT)
                    .setParameter("cutoff", times.getFirst().toInstant().minus(backoff)).setMaxResults(limit)
                    .getResultStream().map(h -> new RetryCandidate(h.getExecutionId(), h.getExecutionId() + "-" + h.getAttemptCount())).toList();
        });
    }

    @Override
    public RetryAdmission claimRetry(String id, String expectedAttemptId, String node, Duration ttl, int maxAttempts, Duration backoff) {
        return claimDeferred(id, expectedAttemptId, node, ttl, maxAttempts, backoff, ExecutionStatus.RETRY_WAIT);
    }

    @Override
    public RetryAdmission claimWaiting(String id, String expectedAttemptId, String node, Duration ttl, int maxAttempts, Duration backoff) {
        return claimDeferred(id, expectedAttemptId, node, ttl, maxAttempts, backoff, ExecutionStatus.WAITING);
    }

    @Override
    public List<RetryCandidate> findWaitingCandidates(int limit) {
        if (limit < 1 || limit > 500) throw new IllegalArgumentException("Invalid waiting scan limit");
        return transaction.execute(tx -> {
            List<RetryCandidate> candidates = new ArrayList<>();
            for (int offset = 0; candidates.size() < limit; offset += 100) {
                var page = em.createQuery("select h from JobExecutionHistoryEntity h where h.status = :state "
                                + "order by h.scheduledAt, h.executionId", JobExecutionHistoryEntity.class)
                        .setParameter("state", ExecutionStatus.WAITING).setFirstResult(offset).setMaxResults(100).getResultList();
                for (var history : page) {
                    var decision = dependencies.evaluate(history.toDomain());
                    // Pending dependencies must not hide ready jobs behind the first scan page.
                    if (decision.ready() || decision.manualReview()) candidates.add(new RetryCandidate(history.getId(), history.getId()+"-"+history.getAttemptCount()));
                    if (candidates.size() == limit) break;
                }
                if (page.size() < 100) break;
            }
            return candidates;
        });
    }

    private RetryAdmission claimDeferred(String id, String expectedAttemptId, String node, Duration ttl,
                                        int maxAttempts, Duration backoff, ExecutionStatus expectedState) {
        if (maxAttempts < 1 || backoff.isNegative()) throw new IllegalArgumentException("Invalid retry policy");
        boolean[] retryCreated = {false};
        var admission = transaction.execute(tx -> {
            boolean active = nodes.lockActive(node);
            var history = em.find(JobExecutionHistoryEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (history == null) throw new EntityNotFoundException("Execution not found: " + id);
            if (!active) return new RetryAdmission(null, history.toDomain(), null, "Node is not ACTIVE");
            if (!Objects.equals(expectedAttemptId, id + "-" + history.getAttemptCount()))
                return new RetryAdmission(null, history.toDomain(), null, "Attempt has changed; stale retry request");
            if (history.getStatus() != expectedState)
                return new RetryAdmission(null, history.toDomain(), null, "Execution is not " + expectedState);
            var latest = history.getAttemptCount() == 0 ? null : em.find(BatchAttemptEntity.class, expectedAttemptId);
            boolean drainDeferred = expectedState == ExecutionStatus.WAITING && latest != null
                    && latest.getStatus() == AttemptState.DEFERRED
                    && latest.getProcessLaunchState() == ProcessLaunchState.NOT_STARTED;
            long deferredCount = em.createQuery("select count(a) from BatchAttemptEntity a where a.executionId=:id and a.status=:state", Long.class)
                    .setParameter("id", id).setParameter("state", AttemptState.DEFERRED).getSingleResult();
            boolean retry = history.getAttemptCount() > 0 && !drainDeferred;
            String blocked = history.isCancellationRequested() ? "Cancellation requested; automatic retry suppressed"
                    : history.getAttemptCount() - deferredCount >= maxAttempts ? "Retry maxAttempts exhausted: " + maxAttempts
                    : retry && history.getRetryWaitSince() == null ? "Retry eligibility timestamp is missing"
                    : !"SHELL".equals(history.getJobType()) ? "No retry executor for job type " + history.getJobType() : null;
            if (blocked != null) {
                history.setStatus(ExecutionStatus.MANUAL_REVIEW);
                history.setMessage((history.getMessage() == null ? "" : history.getMessage()) + "\n[Retry] " + blocked);
                return new RetryAdmission(null, history.toDomain(), null, blocked);
            }
            Instant now = databaseNow(id);
            Instant nextRetryAt = retry ? history.getRetryWaitSince().plus(backoff) : now;
            if (now.isBefore(nextRetryAt))
                return new RetryAdmission(null, history.toDomain(), nextRetryAt, "Retry backoff has not elapsed");
            var lease = em.find(ExecutionLeaseEntity.class, id);
            if (lease == null) throw new IllegalStateException("Execution lease is missing");
            if (lease.getOwnerNode() != null && lease.getExpiresAt().isAfter(now))
                return new RetryAdmission(null, history.toDomain(), lease.getExpiresAt(), "An active lease still exists");
            var claim = acquire(history, lease, node, ttl, now);
            retryCreated[0] = claim != null && retry;
            return new RetryAdmission(claim, history.toDomain(), null, claim == null ? history.getMessage() : "New attempt acquired");
        });
        // Both RETRY_WAIT and capacity-deferred WAITING retries converge here. Count only after commit.
        if (retryCreated[0] && batchMetrics != null) batchMetrics.retryCreated();
        return admission;
    }

    @Override
    public void launchRequested(LeaseClaim claim) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(claim.executionId());
            validLease(history, claim);
            var attempt = currentAttempt(history, claim);
            if (history.isCancellationRequested() || attempt.getStatus() != AttemptState.STARTING
                    || attempt.getProcessLaunchState() != ProcessLaunchState.NOT_REQUESTED) throw new StaleExecutorException();
            attempt.launchRequested();
        });
    }

    @Override
    public Process startProcess(LeaseClaim claim, com.example.scheduler.execution.application.port.ProcessStarter starter)
            throws java.io.IOException {
        Process[] started = new Process[1];
        java.io.IOException[] failed = new java.io.IOException[1];
        try {
            transaction.executeWithoutResult(tx -> {
                // Node is locked by admission before History. Cancellation takes this same History lock.
                var history = locked(claim.executionId());
                validLease(history, claim);
                var attempt = currentAttempt(history, claim);
                if (history.isCancellationRequested() || attempt.getStatus() != AttemptState.STARTING
                        || attempt.getProcessLaunchState() != ProcessLaunchState.START_REQUESTED)
                    throw new StaleExecutorException();
                try { started[0] = starter.start(); }
                catch (java.io.IOException failure) { failed[0] = failure; }
            });
        } catch (RuntimeException failure) {
            // OS creation cannot be rolled back; always return the handle for fenced cleanup.
            if (started[0] == null) throw failure;
        }
        if (failed[0] != null) throw failed[0];
        return started[0];
    }

    @Override
    public void deferForDrain(LeaseClaim claim) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(claim.executionId());
            var lease = validLease(history, claim);
            var attempt = currentAttempt(history, claim);
            if (attempt.getStatus() != AttemptState.STARTING || attempt.getPid() != null
                    || attempt.getWorkerNodeId() != null || attempt.getProcessStartedAt() != null
                    || (attempt.getProcessLaunchState() != ProcessLaunchState.START_REQUESTED
                    && attempt.getProcessLaunchState() != ProcessLaunchState.NOT_REQUESTED)) throw new StaleExecutorException();
            Instant now = databaseNow(claim.executionId());
            if (history.isCancellationRequested()) {
                cancelBeforeStart(history, attempt, lease, now);
                return;
            }
            attempt.deferredByDrain(now);
            history.setStatus(ExecutionStatus.WAITING);
            history.setRetryWaitSince(null);
            history.setEndTime(null);
            history.setDuration(null);
            history.setMessage("[Drain] Process not started; admission deferred; awaiting an ACTIVE Node");
            lease.release(now);
        });
    }

    @Override
    public void startFailed(LeaseClaim claim, String reason, String output, boolean startInvoked) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(claim.executionId());
            var lease = validLease(history, claim);
            var attempt = currentAttempt(history, claim);
            if (attempt.getStatus() != AttemptState.STARTING || attempt.getPid() != null
                    || (attempt.getProcessLaunchState() != ProcessLaunchState.START_REQUESTED
                    && attempt.getProcessLaunchState() != ProcessLaunchState.NOT_REQUESTED)) throw new StaleExecutorException();
            Instant now = databaseNow(claim.executionId());
            if (history.isCancellationRequested() && !startInvoked) {
                cancelBeforeStart(history, attempt, lease, now);
                return;
            }
            if (history.isCancellationRequested()) {
                recordUnconfirmedCancellation(history, attempt, reason);
                lease.release(now);
                return;
            }
            attempt.startFailed(reason, now, startInvoked);
            history.setStatus(ExecutionStatus.RETRY_WAIT);
            history.setRetryWaitSince(now);
            history.setEndTime(local(now));
            history.setDuration(Math.max(0, Duration.between(history.getStartTime(), local(now)).toMillis()));
            history.setMessage((output == null ? "" : output) + "\n[Process start failed] " + reason);
            lease.release(now);
        });
    }

    @Override
    public void started(LeaseClaim claim, long pid, Instant processStartedAt) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(claim.executionId());
            validLease(history, claim);
            var attempt = currentAttempt(history, claim);
            if (attempt.getStatus() != AttemptState.STARTING
                    || attempt.getProcessLaunchState() != ProcessLaunchState.START_REQUESTED) throw new StaleExecutorException();
            attempt.started(pid, processStartedAt);
        });
    }

    @Override
    public void heartbeat(LeaseClaim claim, Duration ttl) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(claim.executionId());
            var lease = validLease(history, claim);
            if (history.isCancellationRequested()) throw new IllegalStateException("Process termination requested by cluster API");
            Instant now = databaseNow(claim.executionId());
            lease.renew(now.plus(ttl));
            currentAttempt(history, claim).heartbeat(now);
        });
    }

    @Override
    public ExecutionStatus finish(LeaseClaim claim, Integer exitCode, String uncertainty, String output) {
        return transaction.execute(tx -> {
            var history = locked(claim.executionId());
            var lease = validLease(history, claim);
            var attempt = currentAttempt(history, claim);
            Instant now = databaseNow(claim.executionId());
            String reason = uncertainty;
            var outcome = history.isCancellationRequested()
                    ? (exitCode != null ? ExecutionStatus.CANCELLED : ExecutionStatus.UNKNOWN)
                    : ExecutionStatus.resultOf(exitCode, reason);
            if (history.isCancellationRequested()) reason = outcome == ExecutionStatus.CANCELLED
                    ? "Cancellation completed; process termination confirmed"
                    : "Cancellation requested; process termination was not confirmed";
            if (history.isCancellationRequested() && outcome == ExecutionStatus.UNKNOWN) {
                recordUnconfirmedCancellation(history, attempt, (output == null ? "" : output) + "\n[System] " + reason);
                lease.release(now);
                return outcome;
            }
            if (reason == null && exitCode != null && exitCode != 0) reason = "Exit code: " + exitCode;
            history.setStatus(outcome);
            history.setEndTime(local(now));
            history.setDuration(Math.max(0, Duration.between(history.getStartTime(), local(now)).toMillis()));
            history.setMessage((output == null ? "" : output) + (reason == null ? "" : "\n[System] " + reason));
            attempt.finish(outcome, exitCode, reason, now);
            lease.release(now);
            return outcome;
        });
    }

    @Override
    public int requestCancellation(String tenant, String group, String name) {
        return transaction.execute(tx -> {
            var running = em.createQuery("select h from JobExecutionHistoryEntity h where h.tenantId = :tenant "
                            + "and h.scheduleGroup = :group and h.scheduleName = :name and h.status in :states order by h.executionId",
                            JobExecutionHistoryEntity.class)
                    .setParameter("tenant", tenant).setParameter("group", group).setParameter("name", name)
                    .setParameter("states", List.of(ExecutionStatus.RUNNING, ExecutionStatus.CANCEL_REQUESTED, ExecutionStatus.WAITING, ExecutionStatus.RETRY_WAIT, ExecutionStatus.SCHEDULED))
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
            running.forEach(history -> {
                history.setCancellationRequested(true);
                if (history.getStatus().hasProcessOwner()) {
                    history.setStatus(ExecutionStatus.CANCEL_REQUESTED);
                    history.setMessage("Cancellation requested; awaiting owner confirmation of process termination");
                } else {
                    history.setStatus(ExecutionStatus.CANCELLED);
                    Instant now = databaseNow(history.getExecutionId());
                    history.setEndTime(local(now));
                    history.setRetryWaitSince(null);
                    history.setMessage("Cancelled while awaiting execution; no active process");
                }
            });
            return running.size();
        });
    }

    @Override
    public int expireLeases() {
        List<String> ids = transaction.execute(tx -> em.createQuery(
                "select h.executionId from JobExecutionHistoryEntity h, ExecutionLeaseEntity l "
                        + "where h.executionId = l.executionId and h.status in :states and l.expiresAt <= current_timestamp order by h.executionId", String.class)
                .setParameter("states", List.of(ExecutionStatus.RUNNING, ExecutionStatus.CANCEL_REQUESTED)).setMaxResults(500).getResultList());
        int expired = 0;
        for (String id : ids) {
            if (Boolean.TRUE.equals(transaction.execute(tx -> {
                var history = locked(id);
                var lease = em.find(ExecutionLeaseEntity.class, id);
                Instant now = databaseNow(id);
                if (!history.getStatus().hasProcessOwner() || lease.getExpiresAt().isAfter(now)) return false;
                expire(history, lease, now);
                return true;
            }))) expired++;
        }
        return expired;
    }

    @Override
    public List<BatchAttempt> attempts(String id) {
        return transaction.execute(tx -> em.createQuery(
                        "select a from BatchAttemptEntity a where a.executionId = :id order by a.attemptNo", BatchAttemptEntity.class)
                .setParameter("id", id).getResultStream().map(BatchAttemptEntity::toDomain).toList());
    }

    @Override
    public Optional<LogicalExecution> findExecution(String id) {
        return histories.findById(id).map(JobExecutionHistoryEntity::toDomain);
    }

    @Override
    public void deferForCapacity(String id, String expectedAttemptId) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(id);
            if ((history.getStatus() == ExecutionStatus.SCHEDULED && expectedAttemptId == null)
                    || (history.getStatus() == ExecutionStatus.RETRY_WAIT
                    && Objects.equals(expectedAttemptId, id + "-" + history.getAttemptCount()))) {
                history.setStatus(ExecutionStatus.WAITING);
                history.setMessage("Waiting for an available Scheduler Node");
            }
        });
    }

    private JobExecutionHistoryEntity locked(String id) {
        var history = em.find(JobExecutionHistoryEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (history == null) throw new StaleExecutorException();
        return history;
    }

    private void cancelBeforeStart(JobExecutionHistoryEntity history, BatchAttemptEntity attempt,
                                   ExecutionLeaseEntity lease, Instant now) {
        attempt.cancelledBeforeStart(now);
        history.setStatus(ExecutionStatus.CANCELLED);
        history.setRetryWaitSince(null);
        history.setEndTime(local(now));
        history.setDuration(Math.max(0, Duration.between(history.getStartTime(), local(now)).toMillis()));
        history.setMessage("Cancelled before process start");
        lease.release(now);
    }

    private void recordUnconfirmedCancellation(JobExecutionHistoryEntity history, BatchAttemptEntity attempt, String reason) {
        history.setStatus(ExecutionStatus.UNKNOWN);
        history.setRetryWaitSince(null);
        history.setEndTime(null);
        history.setDuration(null);
        history.setMessage(reason);
        attempt.expire(reason == null ? null : reason.substring(0, Math.min(1000, reason.length())));
    }

    private ExecutionLeaseEntity validLease(JobExecutionHistoryEntity history, LeaseClaim claim) {
        var lease = em.find(ExecutionLeaseEntity.class, claim.executionId());
        if (lease == null) throw new StaleExecutorException();
        claim.requireCurrent(history.getStatus(), lease.getLeaseToken(), lease.getOwnerNode(), lease.getExpiresAt(), databaseNow(claim.executionId()));
        currentAttempt(history, claim);
        return lease;
    }

    private BatchAttemptEntity currentAttempt(JobExecutionHistoryEntity history, LeaseClaim claim) {
        String expected = history.getExecutionId() + "-" + history.getAttemptCount();
        if (!expected.equals(claim.attemptId())) throw new StaleExecutorException();
        var attempt = em.find(BatchAttemptEntity.class, expected);
        if (attempt == null || attempt.getLeaseToken() != claim.token() || !attempt.getNodeId().equals(claim.nodeId())) throw new StaleExecutorException();
        return attempt;
    }

    private void expire(JobExecutionHistoryEntity history, ExecutionLeaseEntity lease, Instant now) {
        String reason = "Lease expired; process outcome requires reconciliation";
        em.find(BatchAttemptEntity.class, history.getExecutionId() + "-" + history.getAttemptCount()).expire(reason);
        history.setStatus(ExecutionStatus.UNKNOWN);
        history.setMessage(reason);
        // No END_TIME/DURATION: the remote process may still be running.
        lease.expire(now);
    }

    private Instant databaseNow(String id) {
        return clock.now();
    }

    private LocalDateTime local(Instant now) { return LocalDateTime.ofInstant(now, ZoneId.systemDefault()); }
}
