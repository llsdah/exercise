package com.example.scheduler.job.infra.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class WatchdogScheduler {

    private final JobProcessManager jobProcessManager;

    @Scheduled(fixedDelay = 10000) // 10초마다 검사
    public void checkZombieJobs() {
        long now = System.currentTimeMillis();

        jobProcessManager.getRunningProcesses().forEach((processKey, info) -> {
            long duration = (now - info.getStartTime()) / 1000;
            if (duration > info.getTimeoutSeconds()) {
                log.warn("[Watchdog] Job [{}] exceeded timeout ({}s). Killing...", processKey, duration);
                jobProcessManager.killJob(processKey.jobGroup(), processKey.jobName() );
            }
        });
    }
}