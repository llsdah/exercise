package com.example.scheduler.execution.api.dto;

import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.execution.domain.ExecutionStatus;
import java.time.Instant;

// 수정: 기존 JSON 필드는 유지하되 API가 JPA 엔티티를 직접 반환하지 않도록 한다.
public record ExecutionResponse(String id, String tenantId, String scheduleGroup, String scheduleName,
                                String occurrenceKey, Instant scheduledAt, ExecutionStatus status,
                                int attemptCount, long version, Instant retryWaitSince, String triggerNodeId, String fireInstanceId,
                                java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
    public static ExecutionResponse from(LogicalExecution execution) {
        return new ExecutionResponse(execution.getId(), execution.getTenantId(), execution.getScheduleGroup(),
                execution.getScheduleName(), execution.getOccurrenceKey(), execution.getScheduledAt(),
                execution.getStatus(), execution.getAttemptCount(), execution.getVersion(), execution.getRetryWaitSince(),
                execution.getTriggerNodeId(),
                execution.getFireInstanceId(), execution.getStartTime(), execution.getEndTime());
    }
}
