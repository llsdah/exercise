package com.example.scheduler.job.infra.persistent;

import com.example.scheduler.global.converter.BooleanToYNConverter;
import com.example.scheduler.job.domain.Job;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "WFW_SCHEDULE_INFO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobEntity {

    // ==========================================
    // 1. Composite Key (복합키)
    // ==========================================
    @EmbeddedId
    private JobEntityId id;

    // ==========================================
    // 2. Business Columns
    // ==========================================
    @Column(name = "SCHEDULE_TYPE", length = 20, nullable = false)
    private String scheduleType;

    @Column(name = "JOB_TYPE", length = 20, nullable = false)
    private String jobType;

    @Column(name = "JOB_ID", length = 100)
    private String jobId;

    @Column(name = "CRON_EXPRESSION", length = 120, nullable = false)
    private String cronExpression;

    @Column(name = "COMMAND", length = 200, nullable = false)
    private String command;

    @Lob
    @Column(name = "PARAMETERS")
    private String parameters;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SCHEDULE_START_TIME")
    private LocalDateTime scheduleStartTime;

    @Column(name = "SCHEDULE_END_TIME")
    private LocalDateTime scheduleEndTime;

    // Global Converter 적용 (Boolean <-> Y/N)
    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "USE_YN", length = 1, nullable = false)
    @Builder.Default
    private boolean useYn = true;

    // ==========================================
    // 3. Audit Columns (변수명 수정 완료)
    // ==========================================

    // REG_USER_ID -> regUserId
    @Column(name = "REG_USER_ID", length = 50, updatable = false)
    private String regUserId;

    // MODIFY_USER_ID -> modifyUserId
    @Column(name = "MODIFY_USER_ID", length = 50)
    private String modifyUserId;

    // REG_OCCUR_DTTM -> regOccurDttm (String Type)
    @Column(name = "REG_OCCUR_DTTM", length = 23, nullable = false, updatable = false)
    private String regOccurDttm;

    // MODIFY_OCCUR_DTTM -> modifyOccurDttm (String Type)
    @Column(name = "MODIFY_OCCUR_DTTM", length = 23)
    private String modifyOccurDttm;

    // ==========================================
    // 4. Lifecycle Callbacks (자동 주입 로직)
    // ==========================================
    @PrePersist
    public void onPrePersist() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        this.regOccurDttm = now;
        this.modifyOccurDttm = now;

        // 등록자가 없으면 SYSTEM 처리
        if (this.regUserId == null) {
            this.regUserId = "SYSTEM";
        }
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifyOccurDttm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    // ==========================================
    // 5. Domain Mapping (변환 로직)
    // ==========================================
    public static JobEntity from(Job job) {
        return JobEntity.builder()
                .id(new JobEntityId(job.getTenantId(), job.getJobGroup(), job.getJobName()))
                .scheduleType(job.getScheduleType())
                .jobType(job.getJobType())
                .jobId(job.getJobId())
                .cronExpression(job.getCronExpression())
                .command(job.getCommand())
                .parameters(job.getParameters())
                .description(job.getDescription())
                .scheduleStartTime(job.getScheduleStartTime())
                .scheduleEndTime(job.getScheduleEndTime())
                .useYn(job.isUseYn())
                // 도메인 객체의 값을 Entity 필드명에 맞춰 매핑
                .regUserId(job.getRegUserId())      // Domain: createdBy -> Entity: regUserId
                .modifyUserId(job.getModifyUserId())   // Domain: updatedBy -> Entity: modifyUserId
                .build();
    }

    public Job toDomain() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        return new Job(
                this.id.getTenantId(),
                this.id.getScheduleGroup(),
                this.id.getScheduleName(),
                this.scheduleType,
                this.jobType,
                this.jobId,
                this.cronExpression,
                this.command,
                this.parameters,
                this.description,
                this.scheduleStartTime,
                this.scheduleEndTime,
                this.useYn,
                // Entity: regUserId -> Domain: createdBy
                this.regUserId,
                // Entity: regOccurDttm (String) -> Domain: createdAt (LocalDateTime)
                this.regOccurDttm != null ? LocalDateTime.parse(this.regOccurDttm, formatter) : null,
                this.modifyUserId,
                this.modifyOccurDttm != null ? LocalDateTime.parse(this.modifyOccurDttm, formatter) : null
        );
    }
}