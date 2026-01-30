package com.example.scheduler.history.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class JobExecutionHistory {

    private final Long id;
    private final String scheduleGroup;      // [신규] 그룹
    private final String scheduleName;       // 작업명
    private final String scheduleType;
    private final String jobType;       // 작업명
    private final Long executionCount;  // [신규] N번째 실행
    private final String cronExpression;       // [신규] 전체 명령어
    private final String command;       // [신규] 전체 명령어
    private final String parameters;       // [신규] 전체 명령어

    private final ExecutionStatus status; // [신규] 상태 Enum
    private final LocalDateTime startTime; // [신규] 시작 시간
    private final LocalDateTime endTime;   // [신규] 종료 시간
    private final String message;       // 실행 로그 및 에러 메시지

    @Builder
    public JobExecutionHistory(Long id, String scheduleGroup, String scheduleName, String scheduleType, String jobType, Long executionCount, String cronExpression,
                               String command, String parameters, ExecutionStatus status,
                               LocalDateTime startTime, LocalDateTime endTime, String message) {
        this.id = id;
        this.scheduleGroup = scheduleGroup;
        this.scheduleName = scheduleName;
        this.scheduleType = scheduleType;
        this.jobType = jobType;
        this.executionCount = executionCount;
        this.cronExpression = cronExpression;
        this.command = command;
        this.parameters = parameters;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.message = message;
    }
}