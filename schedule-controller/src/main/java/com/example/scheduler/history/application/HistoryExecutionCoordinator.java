package com.example.scheduler.history.application;

import com.example.scheduler.attempt.domain.BatchAttempt;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.history.domain.HistoryExecutionRepository;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.global.config.ExecutionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import com.example.scheduler.history.domain.HistoryExecutionRequest;
import com.example.scheduler.history.domain.ExecutionStatus;
import java.util.List;
import java.util.Optional;

// 수정: application은 실행 유스케이스와 설정값 전달만 담당하고 저장/잠금은 도메인 저장소 계약에 위임한다.
@Service
@RequiredArgsConstructor
public class HistoryExecutionCoordinator {
    private final HistoryExecutionRepository executions;
    private final ExecutionProperties properties;

    public String ensureExecution(HistoryExecutionRequest request) {
        return executions.ensureExecution(request.triggeredBy(properties.nodeId()));
    }

    public Optional<LeaseClaim> claim(String executionId) {
        return executions.claim(executionId, properties.nodeId(), properties.leaseTtl());
    }

    public void verifyLocalWorker(LeaseClaim claim) {
        if (!properties.nodeId().equals(claim.nodeId()))
            throw new IllegalArgumentException("Lease belongs to a different worker node");
    }

    public void started(LeaseClaim claim, long pid, Instant processStartedAt) {
        verifyLocalWorker(claim);
        executions.started(claim, pid, processStartedAt);
    }

    public void launchRequested(LeaseClaim claim) { executions.launchRequested(claim); }
    public void deferForCapacity(String id) { executions.deferForCapacity(id); }

    public void startFailed(LeaseClaim claim, String reason, String output) {
        startFailed(claim, reason, output, false);
    }

    public void startFailed(LeaseClaim claim, String reason, String output, boolean startInvoked) {
        verifyLocalWorker(claim);
        executions.startFailed(claim, reason, output, startInvoked);
    }

    public void heartbeat(LeaseClaim claim) {
        executions.heartbeat(claim, properties.leaseTtl());
    }

    public ExecutionStatus finish(LeaseClaim claim, Integer exitCode, String uncertainty) {
        return finish(claim, exitCode, uncertainty, "");
    }

    public ExecutionStatus finish(LeaseClaim claim, Integer exitCode, String uncertainty, String output) {
        return executions.finish(claim, exitCode, uncertainty, output);
    }

    public int expireLeases() { return executions.expireLeases(); }

    public int requestCancellation(String tenant, String group, String name) {
        return executions.requestCancellation(tenant, group, name);
    }

    public Optional<JobExecutionHistory> findExecution(String executionId) { return executions.findExecution(executionId); }

    public List<BatchAttempt> attempts(String executionId) { return executions.attempts(executionId); }
}
