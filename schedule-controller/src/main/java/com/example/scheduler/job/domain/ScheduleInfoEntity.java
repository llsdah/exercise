package com.example.scheduler.job.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "WFW_SCHEDULE_INFO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert // Default 값 적용을 위해 사용
@DynamicUpdate
public class ScheduleInfoEntity {

    // 1. 복합키 (TENANT_ID, SCHEDULE_GROUP, SCHEDULE_NAME)
    @EmbeddedId
    private ScheduleInfoId id;

    // 2. 일반 컬럼
    @Column(name = "SCHEDULE_TYPE", length = 20, nullable = false)
    private String scheduleType;

    @Column(name = "JOB_TYPE", length = 20, nullable = false)
    private String jobType;

    @Column(name = "JOB_ID", length = 100, nullable = false)
    private String jobId;

    @Column(name = "CRON_EXPRESSION", length = 120, nullable = false)
    private String cronExpression;

    @Column(name = "COMMAND", length = 200, nullable = false)
    private String command;

    // 3. CLOB 매핑 (JSON 파라미터 저장용)
    @Lob
    @Column(name = "PARAMETERS")
    private String parameters; // DB는 CLOB, Java는 String으로 처리

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    // 4. TIMESTAMP 매핑 (실행 기간)
    @Column(name = "SCHEDULE_START_TIME")
    private LocalDateTime scheduleStartTime;

    @Column(name = "SCHEDULE_END_TIME")
    private LocalDateTime scheduleEndTime;

    // 5. Default 값 처리
    @Column(name = "USE_YN", length = 1)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String useYn = "Y";

    // 6. 감사(Audit) 컬럼 - DDL이 VARCHAR2이므로 String 매핑
    @Column(name = "REG_USER_ID", length = 50)
    @ColumnDefault("'SYSTEM'")
    private String regUserId;

    @Column(name = "MODIFY_USER_ID", length = 50)
    private String modifyUserId;

    @Column(name = "REG_OCCUR_DTTM", length = 23, nullable = false)
    private String regOccurDttm; // 예: "2026-01-30 12:00:00.000"

    @Column(name = "MODIFY_OCCUR_DTTM", length = 23)
    private String modifyOccurDttm;

    // =================================================================
    // 엔티티 라이프사이클 Hook (자동 날짜 문자열 변환)
    // DDL이 VARCHAR2(23)이므로 LocalDateTime을 String으로 변환해서 넣어야 함
    // =================================================================
    
    @PrePersist
    public void onPrePersist() {
        String nowStr = getCurrentTimeStr();
        this.regOccurDttm = nowStr;
        if (this.regUserId == null) {
            this.regUserId = "SYSTEM";
        }
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifyOccurDttm = getCurrentTimeStr();
    }

    private String getCurrentTimeStr() {
        // DDL 길이 23자에 맞춤 (yyyy-MM-dd HH:mm:ss.SSS)
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
    
    // 비즈니스 로직용 수정 메서드
    public void updateInfo(String cronExpression, String command, String parameters, String description, String modifyUserId) {
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.description = description;
        this.modifyUserId = modifyUserId;
    }
}