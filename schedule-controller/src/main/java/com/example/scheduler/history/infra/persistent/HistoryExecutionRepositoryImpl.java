package com.example.scheduler.history.infra.persistent;

import com.example.scheduler.history.domain.*;
import com.example.scheduler.attempt.domain.*;
import com.example.scheduler.attempt.infra.persistence.BatchAttemptEntity;
import com.example.scheduler.lease.domain.*;
import com.example.scheduler.lease.infra.persistence.ExecutionLeaseEntity;
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
public class HistoryExecutionRepositoryImpl implements HistoryExecutionRepository {
    @PersistenceContext private EntityManager em;
    private final JobExecutionHistoryJpaRepository histories;
    private final TransactionTemplate transaction;
    private final DatabaseClock clock;

    public HistoryExecutionRepositoryImpl(JobExecutionHistoryJpaRepository histories, PlatformTransactionManager manager,
                                          DatabaseClock clock) {
        this.histories = histories;
        this.clock = clock;
        transaction = new TransactionTemplate(manager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // Keep the existing transaction isolation and History lock for cross-node admission.
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transaction.setTimeout(5);
    }

    private Optional<JobExecutionHistoryEntity> occurrence(HistoryExecutionRequest r) {
        return histories.findByTenantIdAndScheduleGroupAndScheduleNameAndOccurrenceKey(
                r.tenantId(), r.scheduleGroup(), r.scheduleName(), r.occurrenceKey());
    }

    @Override
    public String ensureExecution(HistoryExecutionRequest r) {
        var existing = occurrence(r);
        if (existing.isPresent()) return existing.get().getExecutionId();
        try {
            return transaction.execute(tx -> {
                var history = JobExecutionHistoryEntity.builder()
                        .executionId(UUID.randomUUID().toString())
                        .tenantId(r.tenantId()).scheduleGroup(r.scheduleGroup()).scheduleName(r.scheduleName())
                        .occurrenceKey(r.occurrenceKey()).scheduledAt(r.scheduledAt()).fireInstanceId(r.fireInstanceId()).triggerNodeId(r.triggerNodeId())
                        .scheduleType(r.scheduleType()).jobType(r.jobType())
                        .cronExpression(r.cronExpression() == null || r.cronExpression().isBlank() ? "MANUAL" : r.cronExpression())
                        .command(r.command()).parameters(r.parameters()).status(ExecutionStatus.SCHEDULED)
                        .executionCount(0L).build();
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
            var history = locked(id);
            var lease = em.find(ExecutionLeaseEntity.class, id);
            Instant now = databaseNow(id);
            if (history.getStatus() == ExecutionStatus.RUNNING && !lease.getExpiresAt().isAfter(now)) expire(history, lease, now);
            if (!history.getStatus().canAcquireLease()) return Optional.empty();
            return Optional.ofNullable(acquire(history, lease, node, ttl, now));
        });
    }

    private LeaseClaim acquire(JobExecutionHistoryEntity history, ExecutionLeaseEntity lease, String node, Duration ttl, Instant now) {
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
        return transaction.execute(tx -> em.createQuery("select h from JobExecutionHistoryEntity h where h.status = :state "
                        + "order by h.scheduledAt, h.executionId", JobExecutionHistoryEntity.class)
                .setParameter("state", ExecutionStatus.WAITING).setMaxResults(limit).getResultStream()
                .map(h -> new RetryCandidate(h.getExecutionId(), h.getExecutionId() + "-" + h.getAttemptCount())).toList());
    }

    private RetryAdmission claimDeferred(String id, String expectedAttemptId, String node, Duration ttl,
                                        int maxAttempts, Duration backoff, ExecutionStatus expectedState) {
        if (maxAttempts < 1 || backoff.isNegative()) throw new IllegalArgumentException("Invalid retry policy");
        return transaction.execute(tx -> {
            var history = em.find(JobExecutionHistoryEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (history == null) throw new EntityNotFoundException("Execution not found: " + id);
            if (!Objects.equals(expectedAttemptId, id + "-" + history.getAttemptCount()))
                return new RetryAdmission(null, history.toDomain(), null, "Attempt has changed; stale retry request");
            if (history.getStatus() != expectedState)
                return new RetryAdmission(null, history.toDomain(), null, "Execution is not " + expectedState);
            boolean retry = history.getAttemptCount() > 0;
            String blocked = history.isCancellationRequested() ? "Cancellation requested; automatic retry suppressed"
                    : history.getAttemptCount() >= maxAttempts ? "Retry maxAttempts exhausted: " + maxAttempts
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
            return new RetryAdmission(claim, history.toDomain(), null, claim == null ? history.getMessage() : "New attempt acquired");
        });
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
    public void startFailed(LeaseClaim claim, String reason, String output, boolean startInvoked) {
        transaction.executeWithoutResult(tx -> {
            var history = locked(claim.executionId());
            var lease = validLease(history, claim);
            var attempt = currentAttempt(history, claim);
            if (attempt.getStatus() != AttemptState.STARTING || attempt.getPid() != null
                    || (attempt.getProcessLaunchState() != ProcessLaunchState.START_REQUESTED
                    && attempt.getProcessLaunchState() != ProcessLaunchState.NOT_REQUESTED)) throw new StaleExecutorException();
            Instant now = databaseNow(claim.executionId());
            attempt.startFailed(reason, now, startInvoked);
            history.setStatus(history.isCancellationRequested() ? ExecutionStatus.MANUAL_REVIEW : ExecutionStatus.RETRY_WAIT);
            history.setRetryWaitSince(history.isCancellationRequested() ? null : now);
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
            String reason = history.isCancellationRequested() ? "Process termination requested by cluster API" : uncertainty;
            var outcome = ExecutionStatus.resultOf(exitCode, reason);
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
                    .setParameter("states", List.of(ExecutionStatus.RUNNING, ExecutionStatus.WAITING, ExecutionStatus.RETRY_WAIT, ExecutionStatus.SCHEDULED))
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
            running.forEach(history -> {
                history.setCancellationRequested(true);
                if (history.getStatus() != ExecutionStatus.RUNNING) {
                    history.setStatus(ExecutionStatus.MANUAL_REVIEW);
                    history.setMessage("Cancellation requested while awaiting execution; automatic dispatch suppressed");
                }
            });
            return running.size();
        });
    }

    @Override
    public int expireLeases() {
        List<String> ids = transaction.execute(tx -> em.createQuery(
                "select h.executionId from JobExecutionHistoryEntity h, ExecutionLeaseEntity l "
                        + "where h.executionId = l.executionId and h.status = :state and l.expiresAt <= current_timestamp order by h.executionId", String.class)
                .setParameter("state", ExecutionStatus.RUNNING).setMaxResults(500).getResultList());
        int expired = 0;
        for (String id : ids) {
            if (Boolean.TRUE.equals(transaction.execute(tx -> {
                var history = locked(id);
                var lease = em.find(ExecutionLeaseEntity.class, id);
                Instant now = databaseNow(id);
                if (history.getStatus() != ExecutionStatus.RUNNING || lease.getExpiresAt().isAfter(now)) return false;
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
    public Optional<JobExecutionHistory> findExecution(String id) {
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
