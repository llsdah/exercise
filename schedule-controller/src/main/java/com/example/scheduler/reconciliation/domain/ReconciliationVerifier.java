package com.example.scheduler.reconciliation.domain;
import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.attempt.domain.BatchAttempt;
import java.util.List;
/** Observe only the current attempt's process (node/PID/start identity and OS exit code).
 * Called under the history lock: read-only, bounded, no process launch or external writes.
 * Database errors must propagate. Missing PID is not proof of no launch. */
@FunctionalInterface
public interface ReconciliationVerifier {
    ReconciliationEvidence verify(LogicalExecution execution, List<BatchAttempt> attempts);
}
