package com.example.scheduler.job.application.model;

import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 실행 결과
 */
@Getter
@Builder
public class JobExecutionResult {
    
    private final ExecutionStatus status;
    private final String output;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Long pid;
    
    public static JobExecutionResult skipped(String reason) {
        return JobExecutionResult.builder()
                .status(ExecutionStatus.SKIPPED)
                .output(reason)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
    }
    
    public static JobExecutionResult warning(String reason) {
        return JobExecutionResult.builder()
                .status(ExecutionStatus.WARNING)
                .output(reason)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
    }
}