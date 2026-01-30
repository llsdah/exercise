package com.example.scheduler.system.job;

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

    // 타임아웃 임계치 (예: 1시간 = 3600초)
    // 설정값 주입 (기본값 true)
    @Value("${app.scheduler.timeout:3600}")
    private long TIMEOUT_THRESHOLD_SECONDS = 3600;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("👮 [Watchdog] Starting routine inspection for hung jobs...");

        // 서비스에게 "오래된 작업 죽여라" 명령
        int killedCount = systemJobControlService.terminateHungJobs(TIMEOUT_THRESHOLD_SECONDS);

        if (killedCount > 0) {
            log.warn("👮 [Watchdog] Terminated {} hung jobs. Peace restored.", killedCount);
        } else {
            log.info("👮 [Watchdog] System is healthy. No anomalies found.");
        }
    }
}