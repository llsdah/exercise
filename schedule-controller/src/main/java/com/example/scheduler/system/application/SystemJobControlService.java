package com.example.scheduler.system.application;

import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.error.BusinessException;
import com.example.scheduler.history.application.JobHistoryCommand;
import com.example.scheduler.history.application.JobHistoryService;
import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemJobControlService {

    private final Scheduler scheduler;
    private final JobHistoryService jobHistoryService;

    /**
     * 임계 시간을 초과한 작업을 찾아 Quartz Interrupt 발생
     */
    @Transactional
    public int terminateHungJobs(long limitSeconds) {
        int killedCount = 0;
        try {
            // 현재 실행 중인 모든 작업 조회 (Quartz 메모리/DB 직접 접근)
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();

            for (JobExecutionContext context : executingJobs) {

                if ( context.getJobDetail().getJobClass().getName().contains("HangCheckJob")) {
                    continue;
                }

                long duration = calculateDuration(context);

                if (duration > limitSeconds) {
                    JobKey jobKey = context.getJobDetail().getKey();
                    log.warn("🚨 Job Hang Detected: [{}]{} ({}s). Initiating Kill...", 
                            jobKey.getGroup(), jobKey.getName(), duration);

                    // 강제 종료 신호 전송
                    if (scheduler.interrupt(jobKey)) {
                        killedCount++;
                        // 이력 기록
                        recordHistory(jobKey, context.getFireTime(), duration);
                    } else {
                        throw new BusinessException(ErrorCode.JOB_ALREADY_STOPPED);
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("Watchdog scan failed", e);
        }
        return killedCount;
    }

    // --- Private Helper Methods ---

    private long calculateDuration(JobExecutionContext context) {
        LocalDateTime fireTime = context.getFireTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Duration.between(fireTime, LocalDateTime.now()).getSeconds();
    }

    private void recordHistory(JobKey jobKey, java.util.Date fireDate, long duration) {
        LocalDateTime fireTime = fireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        jobHistoryService.recordHistory(JobHistoryCommand.builder()
                .jobGroup(jobKey.getGroup())
                .jobName(jobKey.getName())
                .startTime(fireTime)
                .endTime(LocalDateTime.now())
                .status(ExecutionStatus.HANG_INTERRUPTED) // Hang에 의한 종료 상태
                .message("Auto-terminated by Watchdog (Timeout exceeded) duration : " + duration)
                .build());
    }
}