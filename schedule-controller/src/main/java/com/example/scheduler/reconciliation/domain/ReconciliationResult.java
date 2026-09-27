package com.example.scheduler.reconciliation.domain;
import com.example.scheduler.history.domain.ExecutionStatus;
import java.time.Instant;
/** status is current logical state; result is the immutable reconciliation decision. */
public record ReconciliationResult(String reconciliationId, String executionId, String attemptId,
        ExecutionStatus result, ExecutionStatus status, boolean changed, String reason, Instant reconciledAt) {}
