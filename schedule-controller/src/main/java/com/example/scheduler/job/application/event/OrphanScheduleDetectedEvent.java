package com.example.scheduler.job.application.event;

import org.quartz.JobKey;

public record OrphanScheduleDetectedEvent(
        JobKey jobKey,
        String tenantId,
        String jobGroup,
        String jobName
) {}