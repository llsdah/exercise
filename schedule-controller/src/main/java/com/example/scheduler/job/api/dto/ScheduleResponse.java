package com.example.scheduler.job.api.dto;

import com.example.scheduler.job.domain.JobStatus;
import com.example.scheduler.job.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * [Quartz 실시간 조회 응답]
 * 작업(Job)의 정의뿐만 아니라, 트리거(Trigger)의 상태와 실행 예정 시간을 포함함.
 */
@Getter
@Builder
public class ScheduleResponse { // [변경] UpcomingJobDto -> ScheduleResponse
    
    // 1. 식별자
    private String tenantId;
    private String jobGroup;  // Display용 그룹명 (prefix 제거됨)
    private String jobName;
    
    // 2. 스케줄링 상태 (동적 정보)
    private JobStatus jobStatus;       // NORMAL, PAUSED, BLOCKED, ERROR ...

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFireTime; // 다음 실행 예정
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prevFireTime; // 마지막 실행

    private String cronExpression;
    
    // 3. 작업 정의 정보 (정적 정보 - 확인용)
    private String jobClass;
    private String description;
    private String command;         // 실행 명령어
    private String parameters;      // 파라미터

    public static ScheduleResponse from(Schedule domain) {
        return ScheduleResponse.builder()
                .tenantId(domain.getTenantId())
                .jobGroup(domain.getJobGroup())
                .jobName(domain.getJobName())
                .jobStatus(domain.getJobStatus())
                .nextFireTime(domain.getNextFireTime())
                .prevFireTime(domain.getPrevFireTime())
                .cronExpression(domain.getCronExpression())
                .jobClass(domain.getJobClass())
                .description(domain.getDescription())
                .command(domain.getCommand())
                .parameters(domain.getParameters())
                .build();
    }

}