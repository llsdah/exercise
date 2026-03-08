package com.example.scheduler.history.infra.aop;

import com.example.scheduler.history.application.JobExecutionHistoryCommand;
import com.example.scheduler.history.application.JobExecutionHistoryService;
import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.job.application.model.JobExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


// history.infra.aop - AOP가 job.application을 가로채서 history로 전달
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class JobExecutionHistoryAspect {

    private final JobExecutionHistoryService historyService;

    @AfterReturning(
            pointcut = "execution(* com.example.scheduler.job.application.JobExecutionRecorder.recordHistory(..))"
    )
    public void afterRecordHistory(JoinPoint joinPoint) {
        log.info("afterRecordHistory execute");
        JobExecution execution = (JobExecution) joinPoint.getArgs()[0];
        try {
            historyService.recordHistory(buildCommand(execution));
        } catch (Exception e) {
            log.error("이력 저장 실패: [{}]{}.{}",
                    execution.getTenantId(), execution.getJobGroup(), execution.getJobName(), e);
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.example.scheduler.job.application.JobExecutionRecorder.recordSimpleHistory(..))"
    )
    public void afterRecordSimpleHistory(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String tenantId = (String) args[0];
        String jobGroup = (String) args[1];
        String jobName  = (String) args[2];
        String message  = (String) args[3];
        try {
            historyService.recordHistory(
                    JobExecutionHistoryCommand.builder()
                            .tenantId(tenantId)
                            .jobGroup(jobGroup)
                            .jobName(jobName)
                            .status(ExecutionStatus.WARNING)
                            .startTime(LocalDateTime.now())
                            .endTime(LocalDateTime.now())
                            .duration(0L)
                            .message(message)
                            .build()
            );
        } catch (Exception e) {
            log.error("유령 스케줄 이력 저장 실패: [{}]{}.{}", tenantId, jobGroup, jobName, e);
        }
    }

    private JobExecutionHistoryCommand buildCommand(JobExecution execution) {
        return JobExecutionHistoryCommand.builder()
                .tenantId(execution.getTenantId())
                .jobGroup(execution.getJobGroup())
                .jobName(execution.getJobName())
                .fireInstanceId(execution.getFireInstanceId())
                .scheduleType(execution.getScheduleType())
                .jobType(execution.getJobType())
                .cronExpression(execution.getCronExpression())
                .command(execution.getCommand() + " "
                        + (execution.getParameters() != null ? execution.getParameters() : ""))
                .parameters(execution.getParameters())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .duration(execution.getDuration())
                .status(execution.getStatus())
                .message(execution.getOutput())
                .build();
    }
}