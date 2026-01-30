package com.example.scheduler.job.infra.scheduler;

import com.example.scheduler.history.application.JobHistoryCommand;
import com.example.scheduler.history.application.JobHistoryService; // 기존에 만드신 서비스
import com.example.scheduler.history.domain.ExecutionStatus;     // SKIPPED 상태 추가 필요
import com.example.scheduler.job.infra.persistent.JobSkipEntity;
import com.example.scheduler.job.infra.persistent.JobSkipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionSkipListener implements TriggerListener {

    private final JobSkipRepository jobSkipRepository;
    private final JobHistoryService jobHistoryService;

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
        String jobGroup = trigger.getJobKey().getGroup();
        String jobName = trigger.getJobKey().getName();

        LocalDateTime scheduledFireTime = context.getScheduledFireTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.SECONDS); // 핵심: 초 단위 이하 버림

        // 1. DB 조회: 이 시간에 스킵하라고 등록된 게 있는지?
        Optional<JobSkipEntity> skipEntityOpt = jobSkipRepository.findByJobGroupIgnoreCaseAndJobNameIgnoreCase(
                jobGroup, jobName);

        log.info("Checking Skip: [{}]{} at {}", jobGroup, jobName, scheduledFireTime);

        if (skipEntityOpt.isPresent()) {
            JobSkipEntity entity = skipEntityOpt.get();
            // [핵심] 슬롯 확인 및 비우기 (Consume)
            if (entity.consumeTime(scheduledFireTime)) {
                log.info("🚫 SKIP MATCHED & CONSUMED: [{}]{} at {}", jobGroup, jobName, scheduledFireTime);
                // 변경사항(null로 바뀐 슬롯) 저장
                jobSkipRepository.save(entity);
                // 이력 저장
                recordSkipHistory(trigger, context, scheduledFireTime);
                return true; // 차단
            }
        }

        return false; // 정상 실행
    }

    private void recordSkipHistory(Trigger trigger, JobExecutionContext context, LocalDateTime executionTime) {
        try {
            String cronExpression = (trigger instanceof CronTrigger ct) ? ct.getCronExpression() : null;
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            jobHistoryService.recordHistory(JobHistoryCommand.builder()
                    .jobGroup(trigger.getJobKey().getGroup())
                    .jobName(trigger.getJobKey().getName())
                    .scheduleType(dataMap.getString("scheduleType") == null ? dataMap.getString("scheduleType") : "SHELL")
                    .jobType(dataMap.getString("jobType") == null ? dataMap.getString("jobType") : "CRON")
                    .fullCommand(cronExpression)
                    .startTime(executionTime) // 시작 예정이었던 시간. (0)
                    .status(ExecutionStatus.SKIPPED) // [중요] 상태값
                    .message("User requested to skip this specific instance.")
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