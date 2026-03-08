package com.example.scheduler.job.application;

import com.example.scheduler.job.application.model.JobExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionRecorder {

    public void recordHistory(JobExecution execution) {
        log.info("이력 기록: [{}]{}.{} status={}",
                execution.getTenantId(), execution.getJobGroup(),
                execution.getJobName(), execution.getStatus());
    }

    public void recordSimpleHistory(String tenantId, String jobGroup, String jobName) {
        log.warn("단순 스케줄 이력 기록: [{}]{}.{}", tenantId, jobGroup, jobName);
    }
}