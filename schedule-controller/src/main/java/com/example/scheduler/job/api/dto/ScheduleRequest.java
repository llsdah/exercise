package com.example.scheduler.job.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;

public record ScheduleRequest(
            @NotBlank String scheduleName,
            String scheduleGroup,
            String scheduleType,

            @NotBlank String jobType, // CRON, ONCE
            String cronExpression,
            //@Valid TriggerRequest trigger,
            @NotBlank String command,
            Map<String, Object> parameters,

            String scheduleUser,

            // 입력 예시: "2026-01-24 10:00:00"
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime

){}
