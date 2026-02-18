package com.example.scheduler.job.application.model;

import java.time.LocalDateTime;

public record JobSkipCommand(
        String tenantId,
        String jobGroup,
        String jobName,
        LocalDateTime fireTime,
        String regUserId
) {}
