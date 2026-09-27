package com.example.scheduler.retry.application;
import com.example.scheduler.global.config.ExecutionProperties;
import com.example.scheduler.global.config.RetryProperties;
import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.HistoryExecutionRepository;
import com.example.scheduler.retry.domain.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
@Service
@EnableConfigurationProperties(RetryProperties.class)
public class RetryService {
    private final HistoryExecutionRepository executions;
    private final ExecutionProperties node;
    private final RetryProperties policy;
    private final RetryExecutor executor;
    private final com.example.scheduler.resource.application.NodeExecutionCapacity capacity;
    public RetryService(HistoryExecutionRepository executions, ExecutionProperties node,
                        RetryProperties policy, RetryExecutor executor, com.example.scheduler.resource.application.NodeExecutionCapacity capacity) {
        this.executions = executions;
        this.node = node;
        this.policy = policy;
        this.executor = executor;
        this.capacity = capacity;
    }
    public List<RetryCandidate> candidates() { return executions.findRetryCandidates(100, policy.backoff()); }
    /** An expected attempt also prevents stale requests from retrying a newer generation. */
    public RetryResult retry(String executionId, String expectedAttemptId) {
        if (expectedAttemptId == null || expectedAttemptId.isBlank()) throw new IllegalArgumentException("attemptId is required");
        var slot = capacity.tryAcquire().orElse(null);
        if (slot == null) {
            executions.deferForCapacity(executionId, expectedAttemptId);
            var current = executions.findExecution(executionId).orElseThrow();
            return new RetryResult(executionId, expectedAttemptId, false, current.getStatus(), null, "Node capacity unavailable");
        }
        try (slot) {
            var admission = executions.claimRetry(executionId, expectedAttemptId, node.nodeId(), node.leaseTtl(),
                    policy.maxAttempts(), policy.backoff());
            if (admission.claim() == null)
                return new RetryResult(executionId, expectedAttemptId, false, admission.execution().getStatus(),
                        admission.nextRetryAt(), admission.reason());
            // Claim is committed before starting the OS process.
            executor.execute(admission.execution(), admission.claim(), slot);
            var current = executions.findExecution(executionId).orElseThrow();
            return new RetryResult(executionId, admission.claim().attemptId(), true, current.getStatus(), null,
                    "New attempt executed; inspect execution/attempt history for its outcome");
        }
    }
    public record RetryResult(String executionId, String attemptId, boolean claimed, ExecutionStatus status,
                              Instant nextRetryAt, String reason) {}
}
