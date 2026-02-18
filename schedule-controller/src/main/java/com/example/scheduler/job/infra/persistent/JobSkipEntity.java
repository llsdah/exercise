package com.example.scheduler.job.infra.persistent;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table(name = "WFW_SCHEDULE_SKIP_INFO")
@IdClass(JobSkipId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobSkipEntity {

    @Id
    @Column(name = "TENANT_ID", length = 14, nullable = false)
    private String tenantId;

    @Id
    @Column(name = "SCHEDULE_GROUP", length = 50, nullable = false)
    private String scheduleGroup;

    @Id
    @Column(name = "SCHEDULE_NAME", length = 200, nullable = false)
    private String scheduleName;

    @Id
    @Column(name = "SKIP_TIME", nullable = false)
    private LocalDateTime skipTime;

    // [추가] Audit Fields (DDL 요구사항 반영)
    @Column(name = "REG_USER_ID", length = 50)
    private String regUserId;

    @Column(name = "MODIFY_USER_ID", length = 50)
    private String modifyUserId;

    @Column(name = "REG_OCCUR_DTTM", length = 23, nullable = false)
    private String regOccurDttm;

    @Column(name = "MODIFY_OCCUR_DTTM", length = 23)
    private String modifyOccurDttm;

    // 엔티티 저장 전 Audit 자동 설정 (JPA Auditing 미사용 시 수동 처리)
    @PrePersist
    public void onPrePersist() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        if (this.regUserId == null) this.regUserId = "SYSTEM"; // 기본값
        this.regOccurDttm = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifyOccurDttm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}