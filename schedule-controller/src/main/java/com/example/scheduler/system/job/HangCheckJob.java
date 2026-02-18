package com.example.scheduler.system.job;

import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.system.application.SystemJobControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution // 왓치독이 중복 실행되지 않도록 방지
public class HangCheckJob implements Job {

    private final SystemJobControlService systemJobControlService;

    private final SchedulerProperties properties;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("👮 [Watchdog] Starting routine inspection for hung jobs...");

        // 서비스에게 "오래된 작업 죽여라" 명령
        int killedCount = systemJobControlService.terminateHungJobs(properties.timeoutSeconds());

        if (killedCount > 0) {
            log.warn("👮 [Watchdog] Terminated {} hung jobs. Peace restored.", killedCount);
        } else {
            log.info("👮 [Watchdog] System is healthy. No anomalies found.");
        }
    }
}