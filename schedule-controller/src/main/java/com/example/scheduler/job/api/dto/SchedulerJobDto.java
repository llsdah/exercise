package com.example.scheduler.job.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record SchedulerJobDto(
        String scheduleGroup,
        String scheduleName,
        String scheduleType,

        String command,
        String parameters, // DB에 저장된 문자열 형태 그대로 반환
        String description,
        String jobType,            // CRON, SIMPLE
        String cronExpression,
        String state,           // WAITING, PAUSED ...

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime previousFireTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime nextFireTime

) {}