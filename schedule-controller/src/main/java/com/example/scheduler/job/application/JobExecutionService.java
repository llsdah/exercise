package com.example.scheduler.job.application;

import com.example.scheduler.job.application.event.JobExecutionCompletedEvent;
import com.example.scheduler.job.application.event.JobSimpleHistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Job 실행 관련 비즈니스 로직
 * - 실행 가능 여부 검증
 * - 이력 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobExecutionRecorder jobExecutionRecorder;

    // AOP 타겟
    @EventListener
    public void onJobExecutionCompleted(JobExecutionCompletedEvent event) {
        jobExecutionRecorder.recordHistory(event.execution());
    }

    // AOP 타겟
    @EventListener
    public void onSimpleHistory(JobSimpleHistoryEvent event) {
        jobExecutionRecorder.recordSimpleHistory(event.tenantId(), event.jobGroup(), event.jobName());
    }

}