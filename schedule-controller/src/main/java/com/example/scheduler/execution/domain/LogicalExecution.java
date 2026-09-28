package com.example.scheduler.execution.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@lombok.AllArgsConstructor
public class LogicalExecution {

    private final String executionId;
    private final String occurrenceKey;
    private final java.time.Instant scheduledAt;
    private final int attemptCount;
    private final long version;
    private final java.time.Instant retryWaitSince;

    private final String tenantId;
    private final String scheduleGroup;
    private final String scheduleName;
    private final LocalDateTime startTime;
    private final String fireInstanceId;
    private final String triggerNodeId;

    private final String scheduleType;
    private final String jobType;
    private final String jobId; // [신규]
    private final Long executionCount;
    private final String cronExpression;
    private final String command;
    private final String parameters;
    private final ExecutionStatus status;
    private final LocalDateTime endTime;
    private final Long duration; // [신규]
    private final String message;

    public String getId() { return executionId; }
}
