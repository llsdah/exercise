package com.example.scheduler.system.application;

import com.example.scheduler.history.application.JobExecutionHistoryCommand;
import com.example.scheduler.history.application.JobExecutionHistoryService;
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
    private final JobExecutionHistoryService jobExecutionHistoryService;

    public int terminateHungJobs(long limitSeconds) {  // 트랜잭션 제거
        int killedCount = 0;
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();

            for (JobExecutionContext context : executingJobs) {

                if (context.getJobDetail().getJobClass().getName().contains("HangCheckJob")) {
                    continue;
                }

                long duration = calculateDuration(context);

                if (duration > limitSeconds) {
                    JobKey jobKey = context.getJobDetail().getKey();

                    if (interruptAndRecord(jobKey, context, duration)) {
                        killedCount++;
                    }
                }

            }

        } catch (SchedulerException e) {
            log.error("Watchdog scan failed", e);
        }
        return killedCount;
    }

    @Transactional
    private boolean interruptAndRecord(JobKey jobKey, JobExecutionContext context, long duration) {
        try {
            if (scheduler.interrupt(jobKey)) {
                recordHistory(jobKey, context.getFireTime(), duration);
                return true;
            }
        } catch (SchedulerException e) {
            log.error("Failed to interrupt job: {}", jobKey, e);
        }
        return false;
    }

    // --- Private Helper Methods ---

    private long calculateDuration(JobExecutionContext context) {
        LocalDateTime fireTime = context.getFireTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Duration.between(fireTime, LocalDateTime.now()).getSeconds();
    }

    private void recordHistory(JobKey jobKey, java.util.Date fireDate, long duration) {
        LocalDateTime fireTime = fireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        jobExecutionHistoryService.recordHistory(JobExecutionHistoryCommand.builder()
                .jobGroup(jobKey.getGroup())
                .jobName(jobKey.getName())
                .startTime(fireTime)
                .endTime(LocalDateTime.now())
                .status(ExecutionStatus.HANG_INTERRUPTED) // Hang에 의한 종료 상태
                .message("Auto-terminated by Watchdog (Timeout exceeded) duration : " + duration)
                .build());
    }
}