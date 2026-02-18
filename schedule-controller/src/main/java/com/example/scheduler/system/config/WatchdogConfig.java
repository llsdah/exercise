package com.example.scheduler.system.config;

import com.example.scheduler.system.job.HangCheckJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatchdogConfig {

    private static final String SYSTEM_GROUP = "SYSTEM::WATCHDOG";
    private static final String JOB_NAME = "hangCheckJob";
    private static final String TRIGGER_NAME = "hangCheckTrigger";

    @Bean
    public JobDetail watchdogJobDetail() {
        return JobBuilder.newJob(HangCheckJob.class)
                .withIdentity(JOB_NAME, SYSTEM_GROUP)
                .storeDurably()
                .withDescription("Detects and terminates stuck jobs")
                .build();
    }

    @Bean
    public Trigger watchdogJobTrigger(JobDetail watchdogJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(watchdogJobDetail)
                .withIdentity(TRIGGER_NAME, SYSTEM_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))  // 매분 0초
                .build();
    }
}