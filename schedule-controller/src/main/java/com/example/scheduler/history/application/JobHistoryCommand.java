package com.example.scheduler.history.application;

import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * [Service DTO]
 * 작업 실행 결과를 서비스에 전달하기 위한 데이터 묶음
 */
@Builder
public record JobHistoryCommand(
    String jobGroup,
    String jobName,
    String scheduleType,
    String jobType,
    String fullCommand,
    LocalDateTime startTime,
    LocalDateTime endTime,
    ExecutionStatus status,
    String message
) {}