package com.example.scheduler.job.infra.listener;

import com.example.scheduler.system.application.SystemMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionSystemOverloadListener extends TriggerListenerSupport {

    private final SystemMonitorService systemMonitorService;

    @Override
    public String getName() {
        return "JobExecutionSystemOverloadListener";
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        try {
            if (systemMonitorService.isSystemOverloaded()) {
                log.warn("시스템 과부하 - 실행 거부: [{}]{}",
                        trigger.getJobKey().getGroup(),
                        trigger.getJobKey().getName());
                return true;
            }
        } catch (Exception e) {
            log.error("과부하 체크 중 오류 - 실행 허용으로 fallback", e);
        }
        return false;
    }
}