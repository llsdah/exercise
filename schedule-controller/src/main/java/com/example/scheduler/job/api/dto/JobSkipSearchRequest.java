package com.example.scheduler.job.api.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

public record JobSkipSearchRequest(
    @RequestParam(name = "tenant_id", required = false, defaultValue = "default")
    String tenantId,
    @RequestParam(name = "schedule_group", required = false, defaultValue = "default")
    String scheduleGroup,
    @RequestParam(name = "schedule_name", required = false, defaultValue = "default")
    String scheduleName,

    @RequestParam(name = "reg_user_id", required = true)
    String regUserId,

    @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime from,
    @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime to


) {}
