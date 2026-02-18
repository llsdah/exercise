package com.example.scheduler.history.application;

import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * [Service DTO]
 * 작업 실행 결과를 서비스에 전달하기 위한 데이터 묶음
 */
@Builder
public record JobExecutionHistoryCommand(
        String tenantId,      // [신규]
        String jobGroup,
        String jobName,
        String fireInstanceId,
        String scheduleType,
        String jobType,
        String jobId,         // [신규]
        String cronExpression,// [신규]
        String command,
        String parameters,    // [신규]
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long duration,        // [신규]
        ExecutionStatus status,
        String message
) {}