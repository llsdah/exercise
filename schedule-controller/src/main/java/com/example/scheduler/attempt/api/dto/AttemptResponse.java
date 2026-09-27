package com.example.scheduler.attempt.api.dto;

import com.example.scheduler.attempt.domain.BatchAttempt;
import com.example.scheduler.attempt.domain.AttemptState;
import java.time.Instant;

// 수정: Attempt 응답 역시 도메인 모델에서 변환한 API DTO를 사용한다.
public record AttemptResponse(String id, String executionId, int attemptNo, String nodeId, long leaseToken,
                              Long pid, Instant startedAt, Instant processStartedAt, Instant endedAt,
                              Integer exitCode, AttemptState status, String failureReason, Instant heartbeatAt,
                              com.example.scheduler.attempt.domain.ProcessLaunchState processLaunchState, String workerNodeId) {
    public static AttemptResponse from(BatchAttempt attempt) {
        return new AttemptResponse(attempt.getId(), attempt.getExecutionId(), attempt.getAttemptNo(),
                attempt.getNodeId(), attempt.getLeaseToken(), attempt.getPid(), attempt.getStartedAt(),
                attempt.getProcessStartedAt(), attempt.getEndedAt(), attempt.getExitCode(), attempt.getStatus(),
                attempt.getFailureReason(), attempt.getHeartbeatAt(), attempt.getProcessLaunchState(), attempt.getWorkerNodeId());
    }
}
