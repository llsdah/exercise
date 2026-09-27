package com.example.scheduler.global.config;

import com.example.scheduler.job.infra.listener.JobExecutionSkipListener;
import com.example.scheduler.job.infra.listener.JobManageTableValidationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.quartz.spi.TriggerFiredBundle;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final JobExecutionSkipListener jobExecutionSkipListener;
    private final JobManageTableValidationListener jobManageTableValidationListener;
    private final AutowireCapableBeanFactory beanFactory;

    /**
     * Spring Boot의 자동 설정을 유지하면서
     * 리스너(Listener) 설정만 추가하는 방식입니다.
     */
    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        return schedulerFactoryBean -> {
            // 수정: Quartz가 매 실행마다 생성자 주입된 새 Job 인스턴스를 사용하도록 한다.
            schedulerFactoryBean.setJobFactory(new SpringBeanJobFactory() {
                @Override
                protected Object createJobInstance(TriggerFiredBundle bundle) {
                    return beanFactory.createBean(bundle.getJobDetail().getJobClass());
                }
            });
            // 기존 설정(DB연결, 트랜잭션 등)은 Spring이 해준 대로 두고
            // 리스너만 쏙 집어넣습니다.
            schedulerFactoryBean.setGlobalTriggerListeners(jobManageTableValidationListener, jobExecutionSkipListener);
            // Overload veto would consume a firing without rescheduling it. Enable only with a durable deferral policy.
            
            // (선택사항) 덮어쓰기 설정 등 필요한 것만 추가
            schedulerFactoryBean.setOverwriteExistingJobs(false);
        };
    }
}
