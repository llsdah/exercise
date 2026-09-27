package com.example.scheduler.history.domain;

import java.time.Instant;

/** Immutable job snapshot persisted before acquiring a process execution lease. */
public record HistoryExecutionRequest(String tenantId, String scheduleGroup, String scheduleName,
                                      String occurrenceKey, Instant scheduledAt, String fireInstanceId,
                                      String scheduleType, String jobType, String cronExpression,
                                      String command, String parameters, String triggerNodeId) {
    public HistoryExecutionRequest triggeredBy(String nodeId) {
        return new HistoryExecutionRequest(tenantId, scheduleGroup, scheduleName, occurrenceKey, scheduledAt,
                fireInstanceId, scheduleType, jobType, cronExpression, command, parameters, nodeId);
    }
    public HistoryExecutionRequest(String tenantId, String scheduleGroup, String scheduleName,
                                   String occurrenceKey, Instant scheduledAt, String fireInstanceId,
                                   String scheduleType, String jobType, String cronExpression, String command, String parameters) {
        this(tenantId, scheduleGroup, scheduleName, occurrenceKey, scheduledAt, fireInstanceId,
                scheduleType, jobType, cronExpression, command, parameters, null);
    }
    public HistoryExecutionRequest {
        for (String value : new String[]{tenantId, scheduleGroup, scheduleName, occurrenceKey, scheduleType, jobType, command}) {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("Execution identity and job snapshot are required");
        }
        java.util.Objects.requireNonNull(scheduledAt, "scheduledAt");
    }
}
