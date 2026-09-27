package com.example.scheduler.reconciliation.domain;
import com.example.scheduler.attempt.domain.BatchAttempt;
import com.example.scheduler.attempt.domain.ProcessLaunchState;
import com.example.scheduler.history.domain.JobExecutionHistory;
import java.util.List;
import static com.example.scheduler.reconciliation.domain.ReconciliationEvidence.ProcessStart.*;
/** START_REQUESTED without a final observation is deliberately inconclusive. */
public class PersistedProcessVerifier implements ReconciliationVerifier {
    @Override
    public ReconciliationEvidence verify(JobExecutionHistory execution, List<BatchAttempt> attempts) {
        var current = attempts.stream().filter(a -> a.getAttemptNo() == execution.getAttemptCount()
                && a.getExecutionId().equals(execution.getExecutionId())).findFirst().orElse(null);
        if (current == null) return new ReconciliationEvidence(UNKNOWN, false, null, "Current attempt is missing");
        var launch = current.getProcessLaunchState();
        if ((launch == ProcessLaunchState.NOT_REQUESTED || launch == ProcessLaunchState.START_FAILED)
                && current.getPid() == null && current.getExitCode() == null)
            return new ReconciliationEvidence(NOT_STARTED, false, null, "Durable launch state: " + launch);
        // An exit code is an OS termination observation; ENDED_AT alone may just mean lost contact.
        return new ReconciliationEvidence(
                launch == ProcessLaunchState.STARTED || current.getPid() != null ? STARTED : UNKNOWN,
                current.getExitCode() != null, current.getExitCode(),
                "Persisted process observations for attempt " + current.getId() + "; launch state=" + launch);
    }
}
