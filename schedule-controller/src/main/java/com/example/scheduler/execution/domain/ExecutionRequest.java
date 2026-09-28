package com.example.scheduler.execution.domain;

import java.time.Instant;

/** Immutable job snapshot persisted before acquiring a process execution lease. */
public record ExecutionRequest(String tenantId, String scheduleGroup, String scheduleName,
                                      String occurrenceKey, Instant scheduledAt, String fireInstanceId,
                                      String scheduleType, String jobType, String cronExpression,
                                      String command, String parameters, String triggerNodeId) {
    public ExecutionRequest triggeredBy(String nodeId) {
        return new ExecutionRequest(tenantId, scheduleGroup, scheduleName, occurrenceKey, scheduledAt,
                fireInstanceId, scheduleType, jobType, cronExpression, command, parameters, nodeId);
    }
    public ExecutionRequest(String tenantId, String scheduleGroup, String scheduleName,
                                   String occurrenceKey, Instant scheduledAt, String fireInstanceId,
                                   String scheduleType, String jobType, String cronExpression, String command, String parameters) {
        this(tenantId, scheduleGroup, scheduleName, occurrenceKey, scheduledAt, fireInstanceId,
                scheduleType, jobType, cronExpression, command, parameters, null);
    }
    public ExecutionRequest {
        for (String value : new String[]{tenantId, scheduleGroup, scheduleName, occurrenceKey, scheduleType, jobType, command}) {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("Execution identity and job snapshot are required");
        }
        java.util.Objects.requireNonNull(scheduledAt, "scheduledAt");
    }
}
