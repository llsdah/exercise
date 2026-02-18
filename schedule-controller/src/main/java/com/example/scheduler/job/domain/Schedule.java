package com.example.scheduler.job.domain;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * [Domain Model]
 * Quartz 메모리에 올라가 있는 실시간 스케줄 정보 (Value Object)
 * - DB Entity인 'Job'과 달리 현재 상태(Status, FireTime)를 포함함.
 */
@Getter
@Builder
public class Schedule {
    private final String tenantId;
    private final String jobGroup;
    private final String jobName;

    // Quartz 엔진 상태
    private final JobStatus jobStatus;
    private final LocalDateTime nextFireTime;
    private final LocalDateTime prevFireTime;
    private final String fireInstanceId;  // [추가] 현재 실행 중인 경우의 고유 식별자

    // 실행 상세
    private final String cronExpression;
    private final String command;
    private final String parameters;
    private final String description;
    private final String jobClass;
    private final int priority;           // [추가] 실행 우선순위
}