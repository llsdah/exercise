package com.example.scheduler.history.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class JobExecutionHistory {

    // PK가 복합키로 바뀌었으므로 단일 ID 제거, TenantID 추가
    private final String tenantId;
    private final String scheduleGroup;
    private final String scheduleName;
    private final LocalDateTime startTime; // PK의 일부
    private final String fireInstanceId;

    private final String scheduleType;
    private final String jobType;
    private final String jobId; // [신규]
    private final Long executionCount;
    private final String cronExpression;
    private final String command;
    private final String parameters;
    private final ExecutionStatus status;
    private final LocalDateTime endTime;
    private final Long duration; // [신규]
    private final String message;

    @Builder
    public JobExecutionHistory(String tenantId, String scheduleGroup, String scheduleName, LocalDateTime startTime,
                               String fireInstanceId, String scheduleType, String jobType, String jobId,
                               Long executionCount, String cronExpression, String command, String parameters,
                               ExecutionStatus status, LocalDateTime endTime, Long duration, String message) {
        this.tenantId = tenantId;
        this.scheduleGroup = scheduleGroup;
        this.scheduleName = scheduleName;
        this.startTime = startTime;
        this.fireInstanceId = fireInstanceId;
        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.jobId = jobId;
        this.executionCount = executionCount;
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.status = status;
        this.endTime = endTime;
        this.duration = duration;
        this.message = message;
    }
}