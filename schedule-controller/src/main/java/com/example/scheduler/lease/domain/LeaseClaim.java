package com.example.scheduler.lease.domain;

import com.example.scheduler.history.domain.ExecutionStatus;

// 수정: 모든 실행자 쓰기에 실행·시도·소유자·토큰을 함께 전달한다.
public record LeaseClaim(String executionId, String attemptId, String nodeId, long token) {
    // 수정: fencing 유효성은 순수 도메인 규칙으로 두고 DB 조회/잠금은 저장소에서 수행한다.
    public void requireCurrent(ExecutionStatus state, long currentToken, String ownerNode,
                               java.time.Instant expiresAt, java.time.Instant now) {
        if (state != ExecutionStatus.RUNNING || token != currentToken
                || !nodeId.equals(ownerNode) || !expiresAt.isAfter(now)) {
            throw new StaleExecutorException();
        }
    }
}
