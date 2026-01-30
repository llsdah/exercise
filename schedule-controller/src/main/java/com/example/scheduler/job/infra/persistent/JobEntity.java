package com.example.scheduler.job.infra.persistent;

import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_job_group_name", columnNames = {"scheduleGroup", "scheduleName"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // [필수] JPA Auditing 활성화
public class JobEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scheduleGroup;

    @Column(nullable = false)
    private String scheduleName;
    @Column(nullable = false)
    private String scheduleType;

    private String jobType;

    private String cronExpression;
    private String command;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    // ================= Audit Fields =================
    @Column(updatable = false)
    private String createdBy;

    @CreatedDate // JPA가 insert 시 자동 주입 (도메인 값이 덮어씌워질 수도 있음)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String updatedBy;

    @LastModifiedDate // JPA가 update 시 자동 주입
    private LocalDateTime updatedAt;

    // ===============================================
    // Domain -> Entity 변환
    // ===============================================
    public static JobEntity from(Job job) {
        return JobEntity.builder()
                .id(job.getId()) // update 시에는 ID가 있어야 함
                .scheduleGroup(job.getJobGroup())
                .scheduleName(job.getJobName())
                .scheduleType(job.getScheduleType())
                .jobType(job.getJobType())

                .cronExpression(job.getCronExpression())
                .command(job.getCommand())
                .parameters(job.getParameters())
                .status(job.getStatus())
                // 도메인 객체의 값을 그대로 DB에 반영
                .createdBy(job.getCreatedBy())
                .createdAt(job.getCreatedAt())
                .updatedBy(job.getUpdatedBy())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    // ===============================================
    // Entity -> Domain 변환
    // ===============================================
    public Job toDomain() {
        return new Job(
                this.id,
                this.scheduleGroup,
                this.scheduleName,
                this.scheduleType,
                this.jobType,
                this.cronExpression,
                this.command,
                this.parameters,
                this.startTime,
                this.endTime,
                this.status,
                this.createdBy,
                this.createdAt,
                this.updatedBy,
                this.updatedAt
        );
    }
}