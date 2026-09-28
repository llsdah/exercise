package com.example.scheduler.execution.infra.persistence;

import com.example.scheduler.execution.domain.ExecutionStatus;
import com.example.scheduler.execution.domain.LogicalExecution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SCHEDULE_EXECUTION_HISTORY", indexes = @Index(name = "IX_HISTORY_WAIT_FIFO", columnList = "STATUS,SCHEDULED_AT,EXECUTION_ID"), uniqueConstraints = @UniqueConstraint(
        name = "UK_HISTORY_OCCURRENCE", columnNames = {"TENANT_ID", "SCHEDULE_GROUP", "SCHEDULE_NAME", "OCCURRENCE_KEY"}))
@Setter(AccessLevel.PACKAGE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobExecutionHistoryEntity {

    @Id @Column(name = "EXECUTION_ID", length = 36)
    private String executionId;
    @Column(name = "OCCURRENCE_KEY", length = 200)
    private String occurrenceKey;
    @Column(name = "SCHEDULED_AT")
    private java.time.Instant scheduledAt;
    @Column(name = "ATTEMPT_COUNT", nullable = false)
    private int attemptCount;
    @Convert(converter = com.example.scheduler.global.converter.BooleanToYNConverter.class)
    @Column(name = "CANCELLATION_REQUESTED", length = 1, nullable = false)
    private boolean cancellationRequested;
    @Version @Column(name = "VERSION", nullable = false)
    private long version;
    @Column(name = "RETRY_WAIT_SINCE")
    @Setter(AccessLevel.PUBLIC)
    private java.time.Instant retryWaitSince;

    public String getId() { return executionId; }

    // Existing history columns are retained.
    @Column(name = "TENANT_ID", length = 14, nullable = false)
    private String tenantId;

    // Schedule Group
    @Column(name = "SCHEDULE_GROUP", length = 50, nullable = false)
    private String scheduleGroup;

    // Schedule Name
    @Column(name = "SCHEDULE_NAME", length = 200, nullable = false)
    private String scheduleName;

    // Actual logical execution start time
    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    // --- 일반 컬럼 ---

    @Column(name = "FIRE_INSTANCE_ID", length = 100)
    private String fireInstanceId;

    @Column(name = "TRIGGER_NODE_ID", length = 100, updatable = false)
    private String triggerNodeId;

    @Column(name = "SCHEDULE_TYPE", length = 20, nullable = false)
    private String scheduleType;

    @Column(name = "EXECUTION_COUNT", nullable = false) // NUMBER(19) -> Long
    @Builder.Default  // Builder 사용 시 기본값 유지
    private Long executionCount = 0L;

    @Column(name = "JOB_TYPE", length = 20, nullable = false)
    private String jobType;

    @Column(name = "JOB_ID", length = 100)
    private String jobId;

    @Column(name = "CRON_EXPRESSION", length = 120, nullable = false)
    private String cronExpression;

    @Column(name = "COMMAND", length = 200, nullable = false)
    private String command;

    // Oracle CLOB 매핑
    @Lob
    @Column(name = "PARAMETERS", columnDefinition = "CLOB")
    private String parameters;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    @Setter(AccessLevel.PUBLIC)
    private ExecutionStatus status;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "DURATION")
    private Long duration; // Duration (milliseconds 등)

    // Oracle CLOB 매핑
    @Lob
    @Column(name = "MESSAGE", columnDefinition = "CLOB")
    private String message;

    // =========================================================
    // 1. Domain -> Entity
    // =========================================================
    public static JobExecutionHistoryEntity from(LogicalExecution domain) {
        return JobExecutionHistoryEntity.builder()
                .executionId(domain.getExecutionId() == null ? java.util.UUID.randomUUID().toString() : domain.getExecutionId())
                .occurrenceKey(domain.getOccurrenceKey()).scheduledAt(domain.getScheduledAt())
                .attemptCount(domain.getAttemptCount()).version(domain.getVersion()).retryWaitSince(domain.getRetryWaitSince())
                .tenantId(domain.getTenantId())
                .scheduleGroup(domain.getScheduleGroup())
                .scheduleName(domain.getScheduleName())
                .fireInstanceId(domain.getFireInstanceId()).triggerNodeId(domain.getTriggerNodeId())
                .scheduleType(domain.getScheduleType())
                .executionCount(domain.getExecutionCount())
                .jobType(domain.getJobType())
                .jobId(domain.getJobId())
                .cronExpression(domain.getCronExpression())
                .command(domain.getCommand())
                .parameters(domain.getParameters())
                .status(domain.getStatus())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .duration(domain.getDuration())
                .message(domain.getMessage())
                .build();
    }

    // =========================================================
    // 2. Entity -> Domain
    // =========================================================
    public LogicalExecution toDomain() {
        return LogicalExecution.builder()
                .executionId(this.executionId).occurrenceKey(this.occurrenceKey).scheduledAt(this.scheduledAt)
                .attemptCount(this.attemptCount).version(this.version).retryWaitSince(this.retryWaitSince)
                .tenantId(this.tenantId)
                .scheduleGroup(this.scheduleGroup)
                .scheduleName(this.scheduleName)
                .fireInstanceId(this.fireInstanceId).triggerNodeId(this.triggerNodeId)
                .scheduleType(this.scheduleType)
                .jobType(this.jobType)
                .jobId(this.jobId)
                .executionCount(this.executionCount)
                .cronExpression(this.cronExpression)
                .parameters(this.parameters)
                .command(this.command)
                .status(this.status)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .duration(this.duration)
                .message(this.message)
                .build();
    }
}
