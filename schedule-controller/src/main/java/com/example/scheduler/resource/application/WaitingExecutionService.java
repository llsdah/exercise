package com.example.scheduler.resource.application;

import com.example.scheduler.global.config.ExecutionProperties;
import com.example.scheduler.global.config.RetryProperties;
import com.example.scheduler.history.domain.HistoryExecutionRepository;
import com.example.scheduler.retry.domain.RetryCandidate;
import com.example.scheduler.retry.domain.RetryExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class WaitingExecutionService {
    private final HistoryExecutionRepository executions;
    private final ExecutionProperties node;
    private final RetryProperties retry;
    private final RetryExecutor executor;
    private final NodeExecutionCapacity capacity;

    public List<RetryCandidate> candidates() { return executions.findWaitingCandidates(100); }

    /** Claims are made in scan order, before asynchronous process execution. */
    public boolean dispatch(RetryCandidate candidate, Executor workers) {
        var slot = capacity.tryAcquire().orElse(null);
        if (slot == null) return false; // No DB claim or mutation on an overloaded node.
        boolean transferred = false;
        try {
            var admission = executions.claimWaiting(candidate.executionId(), candidate.attemptId(), node.nodeId(),
                    node.leaseTtl(), retry.maxAttempts(), retry.backoff());
            if (admission.claim() == null) return true; // Lost race; continue to the next FIFO candidate.
            try {
                workers.execute(() -> {
                    try (slot) { executor.execute(admission.execution(), admission.claim(), slot); }
                    catch (RuntimeException failure) {
                        org.slf4j.LoggerFactory.getLogger(WaitingExecutionService.class).warn(
                                "WAITING execution failed: executionId={}, attemptId={}", candidate.executionId(), admission.claim().attemptId(), failure);
                    }
                });
                transferred = true;
            } catch (java.util.concurrent.RejectedExecutionException stopped) {
                executions.startFailed(admission.claim(), "Dispatcher stopped before process start", "", false);
                return false;
            }
            return true;
        } finally { if (!transferred) slot.close(); }
    }

    public boolean resume(RetryCandidate candidate) { return dispatch(candidate, Runnable::run); }
}
