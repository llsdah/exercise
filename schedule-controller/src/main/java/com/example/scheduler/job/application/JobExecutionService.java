package com.example.scheduler.job.application;

import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.history.application.JobExecutionHistoryCommand;
import com.example.scheduler.history.application.JobExecutionHistoryService;
import com.example.scheduler.job.application.model.JobExecutionInfo;
import com.example.scheduler.job.application.model.JobExecutionResult;
import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobRepository;
import com.example.scheduler.job.domain.JobSkip;
import com.example.scheduler.job.domain.JobSkipRepository;
import com.example.scheduler.system.application.SystemMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Job 실행 관련 비즈니스 로직
 * - 실행 가능 여부 검증
 * - 이력 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobRepository jobRepository;
    private final JobSkipRepository jobSkipRepository;
    private final JobExecutionHistoryService historyService;
    private final SystemMonitorService systemMonitorService;
    private final SchedulerProperties properties;

    /**
     * 시스템 과부하 체크
     */
    public boolean isSystemOverloaded() {
        return systemMonitorService.isSystemOverloaded();
    }

    /**
     * Job 실행 가능 여부 검증
     * @return empty = 실행 가능, present = 실행 불가 사유
     */
    public Optional<String> validateExecution(JobExecutionInfo info) {
        
        // 1. 시스템 과부하 체크
        if (isSystemOverloaded()) {
            log.warn("시스템 과부하로 실행 건너뜀: [{}]{}.{}", 
                    info.getTenantId(), info.getJobGroup(), info.getJobName());
            return Optional.of("System Overload Detected");
        }

        // 2. DB 사용 모드일 때만 검증
        if (properties.checkMetaOnExecute()) {
            Optional<Job> jobOpt = jobRepository.findJob(
                    info.getTenantId(), info.getJobGroup(), info.getJobName());

            // 2-1. DB에 없으면 유령 스케줄
            if (jobOpt.isEmpty()) {
                log.warn("DB에 Job 정보 없음 (유령 스케줄): [{}]{}.{}", 
                        info.getTenantId(), info.getJobGroup(), info.getJobName());
                return Optional.of("Job not found in DB (orphan schedule)");
            }

            Job job = jobOpt.get();

            // 2-2. 비활성화 체크
            if (!job.isUseYn()) {
                log.info("Job 비활성화 상태: [{}]{}.{}", 
                        info.getTenantId(), info.getJobGroup(), info.getJobName());
                return Optional.of("Job is disabled");
            }
        }

        return Optional.empty();
    }

    /**
     * 유령 스케줄 여부 확인
     */
    public boolean isOrphanSchedule(JobExecutionInfo info) {
        if (!properties.checkMetaOnExecute()) {
            return false;
        }
        return jobRepository.findJob(
                info.getTenantId(), 
                info.getJobGroup(), 
                info.getJobName()
        ).isEmpty();
    }

    /**
     * 실행 이력 저장
     */
    public void recordHistory(JobExecutionInfo info, JobExecutionResult result) {
        JobExecutionHistoryCommand command = JobExecutionHistoryCommand.builder()
                .tenantId(info.getTenantId())
                .jobGroup(info.getJobGroup())
                .jobName(info.getJobName())
                .fireInstanceId(info.getFireInstanceId())
                .scheduleType(info.getScheduleType())
                .jobType(info.getJobType())
                .cronExpression(info.getCronExpression())
                .command(info.getCommand() + " " + (info.getParameters() != null ? info.getParameters() : ""))
                .parameters(info.getParameters())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .duration(calculateDuration(result.getStartTime(), result.getEndTime()))
                .status(result.getStatus())
                .message(result.getOutput())
                .build();

        historyService.recordHistory(command);
    }

    private Long calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return null;
        }
        return java.time.Duration.between(start, end).toMillis();
    }
}