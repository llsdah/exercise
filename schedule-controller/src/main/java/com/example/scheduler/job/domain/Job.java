package com.example.scheduler.job.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job {

    private Long id;

    // [식별자]
    private String jobGroup;
    private String jobName;

    // [메타데이터]
    private String scheduleType;
    private String jobType;

    // [실행 정보]
    private String cronExpression;
    private String command;
    private String parameters;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // [상태]
    private JobStatus status;



    // [Audit 정보] - 도메인에서도 이 정보를 들고 있어야 조회 시 보여줄 수 있음
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    // Private Constructor
    private Job(String jobGroup, String jobName, String scheduleType, String jobType,
                String cronExpression, String command, String parameters, LocalDateTime startTime, LocalDateTime endTime,
                String createdBy) { // 생성 시에는 등록자만 알면 됨
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = JobStatus.ACTIVATED;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now(); // 도메인 생성 시점
        this.updatedBy = createdBy;           // 최초 수정자는 등록자와 동일
        this.updatedAt = LocalDateTime.now();
    }

    // Reconstruction Constructor (DB -> Domain 변환용, 모든 필드 포함)
    public Job(Long id, String jobGroup, String jobName, String scheduleType, String jobType,
               String cronExpression, String command, String parameters, LocalDateTime startTime, LocalDateTime endTime, JobStatus status, String createdBy, LocalDateTime createdAt, String updatedBy, LocalDateTime updatedAt) {
        this.id = id;
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    // =========================================================
    // Factory Method (생성)
    // =========================================================
    public static Job create(String jobGroup, String jobName, String scheduleType, String jobType,
                             String cronExpression, String command, String parameters, LocalDateTime startTime, LocalDateTime endTime, String jobUser) { // [변경] 요청자 정보 받음

        if (jobGroup == null || jobGroup.isBlank()) throw new IllegalArgumentException("Job Group is required");
        if (jobName == null || jobName.isBlank()) throw new IllegalArgumentException("Job Name is required");

        String type = (jobType == null || jobType.isBlank()) ? "SHELL" : jobType;
        String user = (jobUser == null) ? "SYSTEM" : jobUser;

        return new Job(jobGroup, jobName, scheduleType, type, cronExpression, command, parameters, startTime, endTime, user);
    }

    // =========================================================
    // Business Logic (수정)
    // =========================================================
    public void update(String cronExpression, String command, String parameters, String JobUser) {
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;

        // [변경] 수정자 및 수정시간 갱신
        this.updatedBy = (JobUser == null) ? "SYSTEM" : JobUser;
        this.updatedAt = LocalDateTime.now();
    }

    // 상태 변경 시에도 수정자는 남겨야 한다면 파라미터 추가 가능
    public void pause() {
        this.status = JobStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void resume() {
        this.status = JobStatus.ACTIVATED;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        this.status = JobStatus.DISABLED;
        this.updatedAt = LocalDateTime.now();
    }
}