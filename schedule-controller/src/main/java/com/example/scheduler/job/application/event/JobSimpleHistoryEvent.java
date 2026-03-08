package com.example.scheduler.job.application.event;

public record JobSimpleHistoryEvent(
        String tenantId,
        String jobGroup,
        String jobName,
        String message
) {}