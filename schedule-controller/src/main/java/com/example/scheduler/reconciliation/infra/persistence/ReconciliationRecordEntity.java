package com.example.scheduler.reconciliation.infra.persistence;
import com.example.scheduler.execution.domain.ExecutionStatus;
import com.example.scheduler.reconciliation.domain.ReconciliationResult;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
/** Immutable per-attempt audit. History is still the logical source of truth. */
@Entity
@Table(name = "BATCH_RECONCILIATION", uniqueConstraints = @UniqueConstraint(
        name = "UK_RECONCILIATION_ATTEMPT", columnNames = {"EXECUTION_ID", "ATTEMPT_ID"}))
public class ReconciliationRecordEntity {
    @Id @Column(name = "RECONCILIATION_ID", length = 36)
    private String reconciliationId;
    @Column(name = "EXECUTION_ID", length = 36, nullable = false)
    private String executionId;
    @Column(name = "ATTEMPT_ID", length = 64, nullable = false)
    private String attemptId;
    @Enumerated(EnumType.STRING) @Column(name = "RESULT", length = 20, nullable = false)
    private ExecutionStatus result;
    @Lob @Column(name = "REASON", nullable = false)
    private String reason;
    @Column(name = "RECONCILED_AT", nullable = false)
    private Instant reconciledAt;
    protected ReconciliationRecordEntity() {}
    ReconciliationRecordEntity(String executionId, String attemptId, ExecutionStatus result, String reason, Instant time) {
        this.reconciliationId = UUID.randomUUID().toString();
        this.executionId = executionId;
        this.attemptId = attemptId;
        this.result = result;
        this.reason = reason;
        this.reconciledAt = time;
    }
    ReconciliationResult response(ExecutionStatus currentStatus, boolean changed) {
        return new ReconciliationResult(reconciliationId, executionId, attemptId, result, currentStatus, changed, reason, reconciledAt);
    }
}
