package com.example.scheduler.reconciliation.infra.persistence;

import com.example.scheduler.execution.infra.persistence.JobExecutionHistoryEntity;

import com.example.scheduler.attempt.infra.persistence.BatchAttemptEntity;
import com.example.scheduler.execution.domain.ExecutionStatus;
import com.example.scheduler.reconciliation.domain.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.*;

@Repository
public class ReconciliationRepositoryImpl implements ReconciliationRepository {
    @PersistenceContext private EntityManager em;
    private final TransactionTemplate transaction;

    public ReconciliationRepositoryImpl(PlatformTransactionManager manager) {
        transaction = new TransactionTemplate(manager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transaction.setTimeout(5);
    }

    @Override
    public List<String> findUnknown(int limit) {
        if (limit < 1 || limit > 500) throw new IllegalArgumentException("limit must be between 1 and 500");
        return transaction.execute(tx -> em.createQuery(
                "select h.executionId from JobExecutionHistoryEntity h where h.status = :status order by h.executionId", String.class)
                .setParameter("status", ExecutionStatus.UNKNOWN).setMaxResults(limit).getResultList());
    }

    @Override
    public Optional<ReconciliationResult> findResult(String id) {
        return findResults(id).stream().findFirst();
    }

    @Override
    public List<ReconciliationResult> findResults(String id) {
        return transaction.execute(tx -> {
            var history = em.find(JobExecutionHistoryEntity.class, id);
            if (history == null) return List.of();
            return em.createQuery("select r from ReconciliationRecordEntity r where r.executionId = :id "
                            + "order by r.reconciledAt desc, r.reconciliationId desc", ReconciliationRecordEntity.class)
                    .setParameter("id", id).getResultStream().map(r -> r.response(history.getStatus(), false)).toList();
        });
    }

    @Override
    public Optional<ReconciliationResult> reconcile(String id, ReconciliationVerifier verifier) {
        return transaction.execute(tx -> {
            // Same lock and ordering as claim/heartbeat/finish/lease expiration.
            var history = em.find(JobExecutionHistoryEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (history == null) return Optional.empty();
            String attemptId = id + "-" + history.getAttemptCount();
            var previous = em.createQuery("select r from ReconciliationRecordEntity r where r.executionId = :id and r.attemptId = :attempt",
                    ReconciliationRecordEntity.class).setParameter("id", id).setParameter("attempt", attemptId)
                    .getResultStream().findFirst().orElse(null);
            if (history.getStatus() != ExecutionStatus.UNKNOWN) {
                return Optional.of(previous == null
                        ? new ReconciliationResult(null, id, attemptId, null, history.getStatus(), false, "Not an UNKNOWN execution", null)
                        : previous.response(history.getStatus(), false));
            }
            if (previous != null) throw new IllegalStateException("Reconciled attempt unexpectedly returned to UNKNOWN");
            if (em.find(BatchAttemptEntity.class, attemptId) == null)
                throw new IllegalStateException("UNKNOWN execution has no current attempt: " + attemptId);
            var attempts = em.createQuery("select a from BatchAttemptEntity a where a.executionId = :id order by a.attemptNo",
                    BatchAttemptEntity.class).setParameter("id", id).getResultStream()
                    .map(BatchAttemptEntity::toDomain).toList();
            var evidence = Objects.requireNonNull(verifier.verify(history.toDomain(), attempts), "Verification evidence");
            var result = evidence.outcome();
            var now = em.createQuery("select current_timestamp from JobExecutionHistoryEntity h where h.executionId = :id",
                    java.sql.Timestamp.class).setParameter("id", id).getSingleResult().toInstant();
            var record = new ReconciliationRecordEntity(id, attemptId, result, evidence.decisionReason(), now);
            history.setStatus(result);
            history.setRetryWaitSince(result == ExecutionStatus.RETRY_WAIT ? now : null);
            em.persist(record);
            // The audit and logical transition commit/roll back together; attempts and leases are untouched.
            em.flush();
            return Optional.of(record.response(result, true));
        });
    }
}
