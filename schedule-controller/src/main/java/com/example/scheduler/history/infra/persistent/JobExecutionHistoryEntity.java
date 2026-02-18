package com.example.scheduler.history.infra.persistent;

import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.JobExecutionHistory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "WFW_SCHEDULE_EXECUTION_HISTORY") // 테이블명 변경
@IdClass(JobExecutionHistoryId.class)                    // 복합 키 클래스 적용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobExecutionHistoryEntity {

    // [PK 1] Tenant ID
    @Id
    @Column(name = "TENANT_ID", length = 14, nullable = false)
    private String tenantId;

    // [PK 2] Schedule Group
    @Id
    @Column(name = "SCHEDULE_GROUP", length = 50, nullable = false)
    private String scheduleGroup;

    // [PK 3] Schedule Name
    @Id
    @Column(name = "SCHEDULE_NAME", length = 200, nullable = false)
    private String scheduleName;

    // [PK 4] Start Time
    @Id
    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    // --- 일반 컬럼 ---

    @Column(name = "FIRE_INSTANCE_ID", length = 100)
    private String fireInstanceId;

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
    public static JobExecutionHistoryEntity from(JobExecutionHistory domain) {
        return JobExecutionHistoryEntity.builder()
                .tenantId(domain.getTenantId())
                .scheduleGroup(domain.getScheduleGroup())
                .scheduleName(domain.getScheduleName())
                .fireInstanceId(domain.getFireInstanceId())
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
    public JobExecutionHistory toDomain() {
        return JobExecutionHistory.builder()
                .tenantId(this.tenantId)
                .scheduleGroup(this.scheduleGroup)
                .scheduleName(this.scheduleName)
                .fireInstanceId(this.fireInstanceId)
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