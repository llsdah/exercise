package com.example.scheduler.history.infra;

import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.JobExecutionHistory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_execution_history", indexes = {
        @Index(name = "idx_history_group_name", columnList = "scheduleGroup, scheduleName"),
        @Index(name = "idx_history_start_time", columnList = "startTime")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobHistoryEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scheduleGroup;

    @Column(nullable = false)
    private String scheduleName;

    @Column(nullable = false)
    private String scheduleType;

    @Column(nullable = false)
    private String jobType;

    @Column(nullable = false)
    private Long executionCount;

    // [신규] 실행 당시의 스케줄 정보 (수동 실행 시 null 가능)
    @Column(length = 200)
    private String cronExpression;

    // [신규] 실행 당시의 파라미터 (스냅샷)
    @Column(columnDefinition = "TEXT")
    private String parameters;

    private String command; // 실행된 명령어 (ex: cmd /c echo ...)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String message;

    // =========================================================
    // 1. Domain -> Entity
    // =========================================================
    public static JobHistoryEntity from(JobExecutionHistory domain) {
        return JobHistoryEntity.builder()
                .scheduleGroup(domain.getScheduleGroup())
                .scheduleName(domain.getScheduleName())
                .executionCount(domain.getExecutionCount())
                .scheduleType(domain.getScheduleType())
                .jobType(domain.getJobType())
                .cronExpression(domain.getCronExpression())
                .parameters(domain.getParameters())
                .command(domain.getCommand())
                .status(domain.getStatus())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .message(domain.getMessage())
                .build();
    }

    // =========================================================
    // 2. Entity -> Domain
    // =========================================================
    public JobExecutionHistory toDomain() {
        return JobExecutionHistory.builder()
                .id(this.id)
                .scheduleGroup(this.scheduleGroup)
                .scheduleName(this.scheduleName)
                .scheduleType(this.scheduleType)
                .jobType(this.jobType)
                .executionCount(this.executionCount)
                // [추가]
                .cronExpression(this.cronExpression)
                .parameters(this.parameters)
                .command(this.command)
                .status(this.status)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .message(this.message)
                .build();
    }
}