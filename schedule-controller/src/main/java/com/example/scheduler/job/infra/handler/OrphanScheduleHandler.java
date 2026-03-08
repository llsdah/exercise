package com.example.scheduler.job.infra.handler;


import com.example.scheduler.job.application.event.JobSimpleHistoryEvent;
import com.example.scheduler.job.application.event.OrphanScheduleDetectedEvent;
import com.example.scheduler.job.infra.scheduler.QuartzJobScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// OrphanScheduleHandler
@Slf4j
@Component
@RequiredArgsConstructor
public class OrphanScheduleHandler {

    private final QuartzJobScheduler quartzJobScheduler;
    private final ApplicationEventPublisher eventPublisher; // Spring 표준

    @Async
    @EventListener
    public void onOrphanDetected(OrphanScheduleDetectedEvent event) {
        String message = "유령 스케줄 PAUSE 실패";
        try {
            quartzJobScheduler.pauseJob(event.jobKey());
        } catch (Exception e) {
            log.error("유령 스케줄 PAUSE 실패: {}", event.jobKey(), e);
        }

        try {
            eventPublisher.publishEvent(new JobSimpleHistoryEvent(event.tenantId(), event.jobGroup(), event.jobName(), message ));

        } catch (Exception e) {
            log.error("유령 스케줄 이력 저장 실패: {}", event.jobKey(), e);
        }
    }
}