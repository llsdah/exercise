package com.example.scheduler.execution.application;

import com.example.scheduler.attempt.domain.BatchAttempt;
import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.execution.application.port.ExecutionRepository;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.global.config.ExecutionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import com.example.scheduler.execution.domain.ExecutionRequest;
import com.example.scheduler.execution.domain.ExecutionStatus;
import java.util.List;
import java.util.Optional;

// 수정: application은 실행 유스케이스와 설정값 전달만 담당하고 저장/잠금은 도메인 저장소 계약에 위임한다.
@Service
@RequiredArgsConstructor
public class ExecutionCoordinator {
    private final ExecutionRepository executions;
    private final ExecutionProperties properties;

    public String ensureExecution(ExecutionRequest request) {
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
    public Process startProcess(LeaseClaim claim, com.example.scheduler.execution.application.port.ProcessStarter starter)
            throws java.io.IOException {
        verifyLocalWorker(claim);
        return executions.startProcess(claim, starter);
    }
    public void deferForDrain(LeaseClaim claim) {
        verifyLocalWorker(claim);
        executions.deferForDrain(claim);
    }
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

    public Optional<LogicalExecution> findExecution(String executionId) { return executions.findExecution(executionId); }

    public List<BatchAttempt> attempts(String executionId) { return executions.attempts(executionId); }
}
