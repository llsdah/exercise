package com.example.scheduler.job.application.model;

import java.time.LocalDateTime;

public record JobInfo(
    String jobGroup,
    String jobName,
    String scheduleType,
    String jobType,
    String jobUser,
    String cronExpression, // null이면 "수동 실행(ONCE)"으로 간주
    String command,
    String parameters, // JSON String으로 변환 완료된 상태

    LocalDateTime startTime,
    LocalDateTime endTime
) {}