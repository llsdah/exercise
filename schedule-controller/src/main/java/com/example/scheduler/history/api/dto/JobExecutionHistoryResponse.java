package com.example.scheduler.history.api.dto;

import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Duration;
import java.time.LocalDateTime;

public record JobExecutionHistoryResponse(
        String tenantId,
        String scheduleGroup,
        String scheduleName,
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

    public static JobExecutionHistoryResponse from(JobExecutionHistory history) {
        // 도메인의 duration 우선 사용, 없으면 계산
        Long duration = history.getDuration();
        if (duration == null && history.getStartTime() != null && history.getEndTime() != null) {
            duration = Duration.between(history.getStartTime(), history.getEndTime()).toMillis();
        }

        return new JobExecutionHistoryResponse(
                history.getTenantId(),
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