package com.example.scheduler.system.config;

import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.system.application.SystemJobControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/** Run on every JVM: Quartz execution queries and interrupts are node-local. */
@Configuration
@RequiredArgsConstructor
public class WatchdogConfig {
    private final SystemJobControlService control;
    private final SchedulerProperties properties;

    @Scheduled(fixedDelay = 60_000)
    public void inspectLocalJobs() {
        control.terminateHungJobs(properties.timeoutSeconds());
    }
}
