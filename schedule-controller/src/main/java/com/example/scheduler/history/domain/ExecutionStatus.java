package com.example.scheduler.history.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionStatus {
    SUCCESS(0, "성공"),
    FAILURE(1, "실패"),
    FORCED_SUCCESS(2, "강제 성공"),
    SKIPPED(3, "건너뜀"),
    HANG_INTERRUPTED(4, "강제 종료"),
    WARNING(99, "경고");

    private final int code;
    private final String description;

    // 코드로 조회
    public static ExecutionStatus fromCode(int code) {
        for (ExecutionStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}