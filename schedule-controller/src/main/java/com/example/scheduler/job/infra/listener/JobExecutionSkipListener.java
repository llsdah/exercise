package com.example.scheduler.job.infra.listener;

import com.example.scheduler.history.application.JobExecutionHistoryCommand;
import com.example.scheduler.history.application.JobExecutionHistoryService;
import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.job.application.event.JobExecutionCompletedEvent;
import com.example.scheduler.job.application.model.JobExecution;
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import com.example.scheduler.job.infra.persistence.JobSkipEntity;
import com.example.scheduler.job.infra.persistence.JobSkipId;
import com.example.scheduler.job.infra.persistence.JobSkipJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionSkipListener extends TriggerListenerSupport {

    private final JobSkipJpaRepository jobSkipJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "JobExecutionSkipListener";
    }

    /**
     * 작업(Job) 실행 직전에 호출됨
     * @return true = 실행 거부(Veto/Skip), false = 정상 실행
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {

        String quartzGroup = trigger.getJobKey().getGroup();
        log.info("quartzGroup : {}",quartzGroup);
        String tenantId = ScheduleKeyPolicy.extractTenantId(quartzGroup);
        String jobGroup = ScheduleKeyPolicy.extractGroup(quartzGroup);
        String jobName = trigger.getJobKey().getName();

        LocalDateTime scheduledFireTime = context.getScheduledFireTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.MINUTES); // 핵심: 초 단위 이하 버림

        // 1. DB 조회: 이 시간에 스킵하라고 등록된 게 있는지?
        Optional<JobSkipEntity> skipEntityOpt = jobSkipJpaRepository.findById(new JobSkipId(tenantId, jobGroup, jobName, scheduledFireTime));
        log.info("Checking Skip: [{}]{} at {}", jobGroup, jobName, scheduledFireTime);

        if (skipEntityOpt.isPresent()) {
            JobSkipEntity entity = skipEntityOpt.get();
            log.warn("🚫 SKIP MATCHED & CONSUMED: [{}]{} at {}", jobGroup, jobName, scheduledFireTime);

            // 삭제
            jobSkipJpaRepository.delete(entity);

            // 이력 저장
            publishSkipEvent(trigger, context, entity);

            return true; // 차단

        }

        return false; // 정상 실행
    }

    private void publishSkipEvent(Trigger trigger, JobExecutionContext context, JobSkipEntity jobSkipEntity) {
        try {
            log.debug("[Skip] trigger type: {}", trigger.getClass().getName());

            String cronExpression = extractCronExpression(trigger);
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            String message = String.format(
                    "User requested to skip this specific instance. [SKIP] Time at %s Registered by %s, Created %s",
                    jobSkipEntity.getSkipTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    jobSkipEntity.getModifyUserId(),
                    jobSkipEntity.getRegOccurDttm()
            );

            // ✅ of() 팩토리로 JobExecution 생성
            JobExecution execution = JobExecution.of(
                    jobSkipEntity.getTenantId(),
                    jobSkipEntity.getScheduleGroup(),
                    jobSkipEntity.getScheduleName(),
                    null,                                                                                // fireInstanceId
                    cronExpression,
                    cronExpression,                                                                      // command
                    dataMap.getString("parameters"),
                    dataMap.getString("jobType")      != null ? dataMap.getString("jobType")      : "CRON",
                    dataMap.getString("scheduleType") != null ? dataMap.getString("scheduleType") : "SHELL",
                    0L,                                                                                  // timeout (스킵이므로 불필요)
                    jobSkipEntity.getSkipTime()                                                          // scheduledFireTime
            );

            // ✅ complete()로 실행 결과 세팅 (스킵 = 시작/종료 동일, pid 없음)
            execution.complete(
                    ExecutionStatus.SKIPPED,
                    message,
                    jobSkipEntity.getSkipTime(),    // startTime
                    jobSkipEntity.getSkipTime(),    // endTime
                    null                            // pid
            );

            eventPublisher.publishEvent(new JobExecutionCompletedEvent(execution));

        } catch (Exception e) {
            log.error("Failed to publish skip event", e);
            // 이력 이벤트 발행 실패가 메인 로직(스킵)에 영향을 주면 안 되므로 예외 삼킴
        }
    }

    private String extractCronExpression(Trigger trigger) {
        if (trigger instanceof CronTrigger ct) {
            return ct.getCronExpression();
        } else if (trigger instanceof CronTriggerImpl cti) {
            return cti.getCronExpression();
        } else {
            // Quartz가 Trigger를 래핑한 경우
            return (String) trigger.getJobDataMap().get("cronExpression");
        }
    }

}