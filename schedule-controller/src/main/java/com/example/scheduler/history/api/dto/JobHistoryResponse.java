package com.example.scheduler.history.api.dto;

import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record JobHistoryResponse(
        Long id,
        String jobGroup,
        String jobName,
        Long executionCount,
        ExecutionStatus status,
        
        // 날짜 포맷팅 (선택사항)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startTime,
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endTime,
        
        Long duration, // 소요시간 (ms) - endTime이 있으면 계산해서 주는 편이 좋음
        String message // 결과 로그
) {
    // Domain -> DTO 변환
    public static JobHistoryResponse from(JobExecutionHistory history) {
        // 소요 시간 계산 (종료 시간이 있을 때만)
        long duration = 0;
        if (history.getStartTime() != null && history.getEndTime() != null) {
            duration = java.time.Duration.between(history.getStartTime(), history.getEndTime()).toMillis();
        }

        return new JobHistoryResponse(
                history.getId(),
                history.getScheduleGroup(),
                history.getScheduleName(),
                history.getExecutionCount(),
                history.getStatus(),
                history.getStartTime(),
                history.getEndTime(),
                duration,
                history.getMessage()
        );
    }
}