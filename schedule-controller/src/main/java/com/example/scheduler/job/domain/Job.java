package com.example.scheduler.job.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job {

    // [식별자 - Composite Key 대응]
    private String tenantId;
    private String jobGroup;  // DB: SCHEDULE_GROUP
    private String jobName;   // DB: SCHEDULE_NAME

    // [비즈니스 식별자 & 메타데이터]
    private String jobId;     // DB: JOB_ID (별도 관리되는 ID)
    private String scheduleType;
    private String jobType;
    private String description; // DB: DESCRIPTION (추가됨)

    // [실행 정보]
    private String cronExpression;
    private String command;
    private String parameters;
    private LocalDateTime scheduleStartTime; // DB: SCHEDULE_START_TIME (이름 일치)
    private LocalDateTime scheduleEndTime;   // DB: SCHEDULE_END_TIME (이름 일치)

    // [상태] - DB 스펙인 USE_YN에 맞춤
    private boolean useYn;

    // [Audit 정보] - DB 컬럼명과 변수명 일치 (CamelCase)
    private String regUserId;       // DB: REG_USER_ID
    private LocalDateTime regOccurDttm; // DB: REG_OCCUR_DTTM (도메인은 날짜 타입 유지)
    private String modifyUserId;    // DB: MODIFY_USER_ID
    private LocalDateTime modifyOccurDttm; // DB: MODIFY_OCCUR_DTTM

    // =========================================================
    // Private Constructor (생성 로직)
    // =========================================================
    private Job(String tenantId, String jobGroup, String jobName, String scheduleType, String jobType,
                String jobId, String cronExpression, String command, String parameters, String description,
                LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime, String regUserId) {

        this.tenantId = (tenantId == null) ? "default" : tenantId;
        this.jobGroup = jobGroup;
        this.jobName = jobName;

        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.jobId = jobId; // 외부에서 주입받거나 없으면 생성

        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.description = description;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;

        this.useYn = true; // 기본값 Y

        // Audit 초기화
        this.regUserId = (regUserId == null) ? "SYSTEM" : regUserId;
        this.regOccurDttm = LocalDateTime.now();
        this.modifyUserId = this.regUserId;
        this.modifyOccurDttm = this.regOccurDttm;
    }

    // =========================================================
    // Private Constructor (생성 로직)
    // =========================================================
    private Job(String tenantId, String jobGroup, String jobName, String scheduleType, String jobType,
                String jobId, String cronExpression, String command, String parameters, String description,
                LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime, String regUserId, LocalDateTime regOccurDttm) {

        this.tenantId = (tenantId == null) ? "default" : tenantId;
        this.jobGroup = jobGroup;
        this.jobName = jobName;

        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.jobId = jobId; // 외부에서 주입받거나 없으면 생성

        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.description = description;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;

        this.useYn = true; // 기본값 Y

        // Audit 초기화
        this.regUserId = (regUserId == null) ? "SYSTEM" : regUserId;
        this.regOccurDttm = regOccurDttm;
        this.modifyUserId = this.regUserId;
        this.modifyOccurDttm = this.regOccurDttm;
    }

    // =========================================================
    // Reconstruction Constructor (Entity -> Domain 변환용)
    // =========================================================
    public Job(String tenantId, String jobGroup, String jobName, String scheduleType, String jobType,
               String jobId, String cronExpression, String command, String parameters, String description,
               LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime, boolean useYn,
               String regUserId, LocalDateTime regOccurDttm, String modifyUserId, LocalDateTime modifyOccurDttm) {
        this.tenantId = tenantId;
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.jobId = jobId;
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.description = description;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.useYn = useYn;
        this.regUserId = regUserId;
        this.regOccurDttm = regOccurDttm;
        this.modifyUserId = modifyUserId;
        this.modifyOccurDttm = modifyOccurDttm;
    }

    // =========================================================
    // Factory Method (생성)
    // =========================================================
    public static Job create(String tenantId, String jobGroup, String jobName,
                             String scheduleType, String jobType,
                             String cronExpression, String command, String parameters, String description,
                             LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime,
                             String regUserId) {

        // 필수값 검증
        if (tenantId == null || tenantId.isBlank()) throw new IllegalArgumentException("Tenant ID is required");
        if (jobGroup == null || jobGroup.isBlank()) throw new IllegalArgumentException("Job Group is required");
        if (jobName == null || jobName.isBlank()) throw new IllegalArgumentException("Job Name is required");

        String type = (jobType == null || jobType.isBlank()) ? "SHELL" : jobType;

        return new Job(tenantId, jobGroup, jobName, scheduleType, type, null,
                cronExpression, command, parameters, description,
                scheduleStartTime, scheduleEndTime, regUserId);
    }


    // =========================================================
    // Factory Method (생성)
    // =========================================================
    public static Job create(String tenantId, String jobGroup, String jobName,
                             String scheduleType, String jobType,
                             String cronExpression, String command, String parameters, String description,
                             LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime,
                             String regUserId, LocalDateTime regOccurDttm) {

        // 필수값 검증
        if (tenantId == null || tenantId.isBlank()) throw new IllegalArgumentException("Tenant ID is required");
        if (jobGroup == null || jobGroup.isBlank()) throw new IllegalArgumentException("Job Group is required");
        if (jobName == null || jobName.isBlank()) throw new IllegalArgumentException("Job Name is required");

        String type = (jobType == null || jobType.isBlank()) ? "SHELL" : jobType;

        return new Job(tenantId, jobGroup, jobName, scheduleType, type, null,
                cronExpression, command, parameters, description,
                scheduleStartTime, scheduleEndTime, regUserId, regOccurDttm);
    }


    // =========================================================
    // Business Logic (수정)
    // =========================================================
    public void update(String cronExpression, String command, String parameters, String description, String modifyUserId) {
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.description = description;

        // 수정자 및 수정시간 갱신
        this.modifyUserId = (modifyUserId == null) ? "SYSTEM" : modifyUserId;
        this.modifyOccurDttm = LocalDateTime.now();
    }

    // 상태 변경 로직 (Enum Status -> boolean useYn 매핑)
    public void activate() {
        this.useYn = true;
        this.modifyOccurDttm = LocalDateTime.now();
    }

    public void deactivate() {
        this.useYn = false;
        this.modifyOccurDttm = LocalDateTime.now();
    }
}