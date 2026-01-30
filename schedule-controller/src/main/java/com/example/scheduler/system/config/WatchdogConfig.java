package com.example.scheduler.system.config;

import com.example.scheduler.system.job.HangCheckJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatchdogConfig {

    // 1. JobDetail 생성 (왓치독 작업 정의)
    @Bean
    public JobDetail watchdogJobDetail() {
        return JobBuilder.newJob(HangCheckJob.class)
                .withIdentity("HANGCHECKJOB", "SYSTEM") // 그룹명을 SYSTEM으로 분리
                .storeDurably()
                .withDescription("Detects and terminates stuck jobs")
                .build();
    }

    // 2. Trigger 생성 (1분마다 실행)
    @Bean
    public Trigger watchdogJobTrigger(JobDetail watchdogJobDetail) {
        // Cron Schedule: 1분마다 실행 (0 * * * * ?)
        // 또는 Simple Schedule 사용 가능
        return TriggerBuilder.newTrigger()
                .forJob(watchdogJobDetail)
                .withIdentity("hangCheckTrigger", "SYSTEM")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/30 * * * * ?"))
                .build();
    }
}