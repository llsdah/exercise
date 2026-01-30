package com.example.scheduler.job.domain;

public enum JobStatus {
    ACTIVATED, // 정상 작동 중
    PAUSED,    // 일시 정지 (트리거 해제됨)
    DISABLED   // 비활성화 (아예 실행 대상 아님)
}