package com.example.scheduler.global.config;

import com.example.scheduler.job.infra.scheduler.ExecutionSkipListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final ExecutionSkipListener executionSkipListener;

    /**
     * Spring Boot의 자동 설정을 유지하면서
     * 리스너(Listener) 설정만 추가하는 방식입니다.
     */
    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        return schedulerFactoryBean -> {
            // 기존 설정(DB연결, 트랜잭션 등)은 Spring이 해준 대로 두고
            // 리스너만 쏙 집어넣습니다.
            schedulerFactoryBean.setGlobalTriggerListeners(executionSkipListener);
            
            // (선택사항) 덮어쓰기 설정 등 필요한 것만 추가
            schedulerFactoryBean.setOverwriteExistingJobs(true);
        };
    }
}