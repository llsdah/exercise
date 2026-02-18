package com.example.scheduler.job.infra.scheduler;

import com.example.scheduler.history.application.JobExecutionHistoryCommand;
import com.example.scheduler.history.application.JobExecutionHistoryService; // 기존에 만드신 서비스
import com.example.scheduler.history.domain.ExecutionStatus;     // SKIPPED 상태 추가 필요
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import com.example.scheduler.job.infra.persistent.JobSkipEntity;
import com.example.scheduler.job.infra.persistent.JobSkipId;
import com.example.scheduler.job.infra.persistent.JobSkipJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionSkipListener implements TriggerListener {

    private final JobSkipJpaRepository jobSkipJpaRepository;
    private final JobExecutionHistoryService jobExecutionHistoryService;

    @Override
    public String getName() {
        return "ExecutionSkipListener";
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
            recordSkipHistory(trigger, context, entity);

            return true; // 차단

        }

        return false; // 정상 실행
    }

    private void recordSkipHistory(Trigger trigger, JobExecutionContext context, JobSkipEntity jobSkipEntity) {
        try {
            String cronExpression = (trigger instanceof CronTrigger ct) ? ct.getCronExpression() : null;
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            jobExecutionHistoryService.recordHistory(JobExecutionHistoryCommand.builder()
                    .tenantId(jobSkipEntity.getTenantId())
                    .jobGroup(jobSkipEntity.getScheduleGroup())
                    .jobName(jobSkipEntity.getScheduleName())
                    .scheduleType(dataMap.getString("scheduleType") == null ? dataMap.getString("scheduleType") : "SHELL")
                    .jobType(dataMap.getString("jobType") == null ? dataMap.getString("jobType") : "CRON")
                    .command(cronExpression)
                    .startTime(jobSkipEntity.getSkipTime()) // 시작 예정이었던 시간. (0)
                    .status(ExecutionStatus.SKIPPED) // [중요] 상태값
                    .message( String.format("User requested to skip this specific instance. [SKIP] Time at %s Registered by %s, Created %s",jobSkipEntity.getSkipTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), jobSkipEntity.getModifyUserId(), jobSkipEntity.getRegOccurDttm()))
                    .build());
        } catch (Exception e) {
            log.error("Failed to save skip history", e);
            // 이력 저장 실패가 메인 로직(스킵)에 영향을 주면 안 되므로 예외 삼킴
        }
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {}
    @Override
    public void triggerMisfired(Trigger trigger) {}
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {}
}