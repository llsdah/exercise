package com.example.scheduler.job.application.model;

import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Job 실행 인스턴스
 * - 실행 정의 (Quartz Context 추출)
 * - 실행 결과 (프로세스 완료 후 세팅)
 */
@Getter
public class JobExecution {

    // 실행 정의
    private final String tenantId;
    private final String jobGroup;
    private final String jobName;
    private final String fireInstanceId;
    private final String cronExpression;
    private final String command;
    private final String parameters;
    private final String jobType;
    private final String scheduleType;
    private final long timeout;
    private final LocalDateTime scheduledFireTime;

    // 실행 결과 - complete() 후 세팅
    private ExecutionStatus status;
    private String output;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long pid;

    private JobExecution(String tenantId, String jobGroup, String jobName,
                         String fireInstanceId, String cronExpression,
                         String command, String parameters,
                         String jobType, String scheduleType,
                         long timeout, LocalDateTime scheduledFireTime) {
        this.tenantId = tenantId;
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.fireInstanceId = fireInstanceId;
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.jobType = jobType;
        this.scheduleType = scheduleType;
        this.timeout = timeout;
        this.scheduledFireTime = scheduledFireTime;
    }

    public static JobExecution of(String tenantId, String jobGroup, String jobName,
                                  String fireInstanceId, String cronExpression,
                                  String command, String parameters,
                                  String jobType, String scheduleType,
                                  long timeout, LocalDateTime scheduledFireTime) {
        return new JobExecution(tenantId, jobGroup, jobName, fireInstanceId,
                cronExpression, command, parameters,
                jobType, scheduleType, timeout, scheduledFireTime);
    }

    public void complete(ExecutionStatus status, String output,
                         LocalDateTime startTime, LocalDateTime endTime, Long pid) {
        this.status = status;
        this.output = output;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pid = pid;
    }

    public Long getDuration() {
        if (startTime == null || endTime == null) return null;
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
}