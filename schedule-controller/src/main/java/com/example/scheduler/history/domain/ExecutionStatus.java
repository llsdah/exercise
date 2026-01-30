package com.example.scheduler.history.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionStatus {
    SUCCESS(0, "성공"),
    FAILURE(1, "실패"),
    FORCED_SUCCESS(2, "강제 성공"), // 운영자가 수동으로 상태 변경 시 사용
    SKIPPED(3, "SKIP"),
    HANG_INTERRUPTED(4,"HANG_INTERRUPTED"),
    WARNING(99, "경고");          // 필요 시 확장 가능

    private final int code;
    private final String description;
}