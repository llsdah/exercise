package com.example.scheduler.attempt.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;

// 수정: 실행 시도 조회 모델은 영속성 어노테이션 없이 도메인에 둔다.
@Getter
@AllArgsConstructor
public class BatchAttempt {
    private final String id;
    private final String executionId;
    private final int attemptNo;
    private final String nodeId;
    private final long leaseToken;
    private final Long pid;
    private final Instant startedAt;
    private final Instant processStartedAt;
    private final Instant endedAt;
    private final Integer exitCode;
    private final AttemptState status;
    private final String failureReason;
    private final Instant heartbeatAt;
    private final ProcessLaunchState processLaunchState;
    private final String workerNodeId;
}
