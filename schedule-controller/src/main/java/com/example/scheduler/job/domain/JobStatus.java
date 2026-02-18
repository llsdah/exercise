package com.example.scheduler.job.domain;

public enum JobStatus {
    ACTIVATED, // 정상 작동 중
    PAUSED,    // 일시 정지 (트리거 해제됨)
    DISABLED   // 비활성화 (아예 실행 대상 아님)
    ;
    // Quartz TriggerState와 매핑 (infra에서 사용)
    public static JobStatus fromQuartzState(String triggerState) {
        if (triggerState == null) {
            return DISABLED;
        }
        return switch (triggerState.toUpperCase()) {
            case "NORMAL", "WAITING", "ACQUIRED" -> ACTIVATED;
            case "PAUSED", "PAUSED_BLOCKED" -> PAUSED;
            case "BLOCKED" -> ACTIVATED;  // 실행 중이지만 활성 상태
            case "ERROR", "COMPLETE", "NONE" -> DISABLED;
            default -> DISABLED;
        };
    }
}