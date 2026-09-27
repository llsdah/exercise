package com.example.scheduler.history.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionStatus {
    SCHEDULED(6, "예약"),
    RUNNING(7, "실행 중"),
    RETRY_WAIT(8, "재시도 대기"),
    MANUAL_REVIEW(9, "운영자 확인 필요"),
    WAITING(10, "자원 대기"),
    SUCCESS(0, "성공"),
    FAILURE(1, "실패"),
    UNKNOWN(5, "결과 불명"), // 수정: 종료/통신 장애로 업무 결과를 확정할 수 없는 실행을 구분한다.
    FORCED_SUCCESS(2, "강제 성공"),
    SKIPPED(3, "건너뜀"),
    HANG_INTERRUPTED(4, "강제 종료"),
    WARNING(99, "경고");

    private final int code;
    private final String description;

    public boolean canAcquireLease() { return this == SCHEDULED; }

    public static ExecutionStatus resultOf(Integer exitCode, String uncertainty) {
        return uncertainty != null || exitCode == null ? UNKNOWN : exitCode == 0 ? SUCCESS : FAILURE;
    }

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
