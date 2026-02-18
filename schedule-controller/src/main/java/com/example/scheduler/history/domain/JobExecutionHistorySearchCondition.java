package com.example.scheduler.history.domain;

import java.time.LocalDateTime;

public record JobExecutionHistorySearchCondition(
        String tenantId,        // 필수
        String scheduleGroup,   // 옵션 (Eq)
        String scheduleName,    // 옵션 (Like)
        String fireInstanceId,  // null 허용
        ExecutionStatus status, // [추가] 옵션 (Eq)
        LocalDateTime from,     // [추가] 기간 검색 (Start Time >=)
        LocalDateTime to        // [추가] 기간 검색 (Start Time <=)
) {
    public JobExecutionHistorySearchCondition {
        // 기본값 및 검증
        tenantId = (tenantId == null || tenantId.isBlank()) ? "default" : tenantId.toLowerCase();
        scheduleGroup = scheduleGroup != null ? scheduleGroup.toLowerCase() : null;
        scheduleName = scheduleName != null ? scheduleName.toLowerCase() : null;

        // 기간 검증
        if (from != null && to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("'to' must be after 'from'");
        }
    }

}