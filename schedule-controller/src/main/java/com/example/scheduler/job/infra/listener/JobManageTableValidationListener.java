package com.example.scheduler.job.infra.listener;

import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.job.application.event.OrphanScheduleDetectedEvent;
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobRepository;
//import com.example.scheduler.job.infra.listener.event.OrphanScheduleDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobManageTableValidationListener extends TriggerListenerSupport {

    private final JobRepository jobRepository;
    private final SchedulerProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "JobExecutionManagementTableCheckListener";
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        try {
            if (!properties.checkMetaOnExecute()) {
                return false;
            }

            JobKey jobKey = trigger.getJobKey();
            String quartzGroup = jobKey.getGroup();
            String tenantId = ScheduleKeyPolicy.extractTenantId(quartzGroup);
            String jobGroup = ScheduleKeyPolicy.extractGroup(quartzGroup);
            String jobName = jobKey.getName();

            // DB 조회 1회
            Optional<Job> jobOpt = jobRepository.findJob(tenantId, jobGroup, jobName);

            // 1. DB에 없음 → 유령 스케줄
            if (jobOpt.isEmpty()) {
                log.warn("유령 스케줄 감지 - 실행 거부: [{}]{}", quartzGroup, jobName);
                eventPublisher.publishEvent(
                        new OrphanScheduleDetectedEvent(jobKey, tenantId, jobGroup, jobName)
                );
                return true;
            }

            // 2. DB에 있지만 비활성화
            if (!jobOpt.get().isUseYn()) {
                log.info("비활성화 Job - 실행 거부: [{}]{}", quartzGroup, jobName);
                return true;
            }

        } catch (Exception e) {
            log.error("메타 테이블 체크 중 오류 - 실행 허용으로 fallback", e);
        }
        return false;
    }
}