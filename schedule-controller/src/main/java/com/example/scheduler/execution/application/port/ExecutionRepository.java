package com.example.scheduler.execution.application.port;

import com.example.scheduler.execution.domain.LogicalExecution;

import com.example.scheduler.execution.domain.ExecutionStatus;

import com.example.scheduler.execution.domain.ExecutionRequest;

import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.attempt.domain.BatchAttempt;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

// 수정: 기존 JobRepository 구조와 같이 영속 구현을 감추고 원자적 실행권 변경 계약만 노출한다.
public interface ExecutionRepository {
    String ensureExecution(ExecutionRequest request);
    Optional<LeaseClaim> claim(String executionId, String nodeId, Duration leaseTtl);
    void launchRequested(LeaseClaim claim);
    Process startProcess(LeaseClaim claim, ProcessStarter starter) throws java.io.IOException;
    default void startFailed(LeaseClaim claim, String reason, String output) { startFailed(claim, reason, output, false); }
    void startFailed(LeaseClaim claim, String reason, String output, boolean startInvoked);
    void deferForDrain(LeaseClaim claim);
    com.example.scheduler.retry.domain.RetryAdmission claimRetry(String executionId, String expectedAttemptId,
            String nodeId, Duration leaseTtl, int maxAttempts, Duration backoff);
    List<com.example.scheduler.retry.domain.RetryCandidate> findRetryCandidates(int limit, Duration backoff);
    List<com.example.scheduler.retry.domain.RetryCandidate> findWaitingCandidates(int limit);
    com.example.scheduler.retry.domain.RetryAdmission claimWaiting(String executionId, String expectedAttemptId,
            String nodeId, Duration leaseTtl, int maxAttempts, Duration backoff);
    default void deferForCapacity(String executionId) { deferForCapacity(executionId, null); }
    void deferForCapacity(String executionId, String expectedAttemptId);
    void started(LeaseClaim claim, long pid, Instant processStartedAt);
    void heartbeat(LeaseClaim claim, Duration leaseTtl);
    ExecutionStatus finish(LeaseClaim claim, Integer exitCode, String uncertainty, String output);
    int expireLeases();
    int requestCancellation(String tenant, String group, String name);
    Optional<LogicalExecution> findExecution(String executionId);
    List<BatchAttempt> attempts(String executionId);
}
