package com.example.scheduler.attempt.domain;

import com.example.scheduler.execution.domain.ExecutionStatus;

// 수정: Process.start 전의 영속 기록과 결과 불명 상태를 별도로 보존한다.
public enum AttemptState {
    STARTING, RUNNING, SUCCESS, FAILED, UNKNOWN, DEFERRED, CANCELLED;

    // 수정: 실행 결과를 시도 상태로 변환하는 규칙을 도메인 내부에 둔다.
    public static AttemptState resultOf(ExecutionStatus outcome) {
        return switch (outcome) {
            case SUCCESS -> SUCCESS;
            case FAILURE -> FAILED;
            case UNKNOWN -> UNKNOWN;
            case CANCELLED -> CANCELLED;
            default -> throw new IllegalArgumentException("Not an execution outcome: " + outcome);
        };
    }
}
