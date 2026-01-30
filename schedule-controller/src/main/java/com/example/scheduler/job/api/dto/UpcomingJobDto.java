package com.example.scheduler.job.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record UpcomingJobDto(
        String scheduleGroup,
        String scheduleName,
        String type, // CRON, SIMPLE
        String cronExpression, // 정보 제공용
        String command,
        String parameters,

        String triggerGroup,
        String triggerName,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime nextFireTime, // 예정 시간
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime prevFireTime  // 참고용 (직전 실행 시간)
) {}