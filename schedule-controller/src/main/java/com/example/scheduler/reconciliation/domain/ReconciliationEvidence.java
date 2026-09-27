package com.example.scheduler.reconciliation.domain;
import com.example.scheduler.history.domain.ExecutionStatus;
import java.util.Objects;
/** Process observations only; business outcomes are outside the platform's responsibility. */
public record ReconciliationEvidence(ProcessStart processStart, boolean processTerminated, Integer exitCode, String reason) {
    public enum ProcessStart { NOT_STARTED, STARTED, UNKNOWN }
    public ReconciliationEvidence {
        Objects.requireNonNull(processStart, "Process start observation");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Observation reason is required");
    }
    public ExecutionStatus outcome() {
        if (processStart == ProcessStart.NOT_STARTED)
            return processTerminated || exitCode != null ? ExecutionStatus.MANUAL_REVIEW : ExecutionStatus.RETRY_WAIT;
        if (processTerminated && exitCode != null)
            return exitCode == 0 ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILURE;
        return ExecutionStatus.MANUAL_REVIEW;
    }
    public String decisionReason() {
        return "processStart=" + processStart + ", processTerminated=" + processTerminated
                + ", exitCode=" + exitCode + "; " + reason;
    }
}
