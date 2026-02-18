package com.example.scheduler.job.application.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Quartz Context에서 추출한 실행 정보
 */
@Getter
@Builder
public class JobExecutionInfo {
    
    private final String tenantId;
    private final String jobGroup;
    private final String jobName;
    private final String fireInstanceId;
    
    private final String cronExpression;
    private final String command;
    private final String parameters;
    private final String jobType;
    private final String scheduleType;
    private final long timeout;
    
    private final LocalDateTime scheduledFireTime;
}