package com.example.scheduler.job.infra.executor;

import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.job.application.model.JobExecutionInfo;
import com.example.scheduler.job.application.model.JobExecutionResult;
import com.example.scheduler.job.application.JobExecutionService;
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Quartz Job 실행기
 * - Quartz 연동
 * - 프로세스 실행
 * - 비즈니스 로직은 JobExecutionService에 위임
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShellCommandJob implements Job, InterruptableJob {

    private final JobExecutionService jobExecutionService;
    private final JobProcessManager jobProcessManager;

    private volatile boolean interrupted = false;

    @Override
    public void execute(JobExecutionContext context) {
        // 1. 실행 정보 추출
        JobExecutionInfo info = extractExecutionInfo(context);

        log.info("========== Job 실행 시작: [{}]{}.{} ==========",
                info.getTenantId(), info.getJobGroup(), info.getJobName());

        // 2. 실행 가능 여부 검증
        Optional<String> skipReason = jobExecutionService.validateExecution(info);

        if (skipReason.isPresent()) {
            JobExecutionResult result = JobExecutionResult.skipped(skipReason.get());
            jobExecutionService.recordHistory(info, result);

            // 유령 스케줄이면 Quartz에서 삭제
            if (jobExecutionService.isOrphanSchedule(info)) {
                deleteOrphanSchedule(context);
            }
            return;
        }

        // 3. 프로세스 실행
        JobExecutionResult result = executeProcess(info, context);

        // 4. 이력 저장
        jobExecutionService.recordHistory(info, result);

        log.info("========== Job 실행 완료: [{}]{}.{} - {} ==========",
                info.getTenantId(), info.getJobGroup(), info.getJobName(), result.getStatus());
    }

    /**
     * Quartz Context에서 실행 정보 추출
     */
    private JobExecutionInfo extractExecutionInfo(JobExecutionContext context) {
        String quartzGroup = context.getJobDetail().getKey().getGroup();
        JobDataMap data = context.getJobDetail().getJobDataMap();

        LocalDateTime scheduledFireTime = context.getScheduledFireTime() != null
                ? context.getScheduledFireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null;

        return JobExecutionInfo.builder()
                .tenantId(ScheduleKeyPolicy.extractTenantId(quartzGroup))
                .jobGroup(ScheduleKeyPolicy.extractGroup(quartzGroup))
                .jobName(context.getJobDetail().getKey().getName())
                .fireInstanceId(context.getFireInstanceId())
                .cronExpression(data.getString("cronExpression"))
                .command(data.getString("command"))
                .parameters(data.getString("parameters"))
                .jobType(data.getString("jobType"))
                .scheduleType(data.getString("scheduleType"))
                .timeout(data.containsKey("timeout") ? data.getLong("timeout") : 3600)
                .scheduledFireTime(scheduledFireTime)
                .build();
    }

    /**
     * 프로세스 실행
     */
    private JobExecutionResult executeProcess(JobExecutionInfo info, JobExecutionContext context) {
        Process process = null;
        StringBuilder output = new StringBuilder();
        ExecutionStatus status = ExecutionStatus.SUCCESS;
        LocalDateTime startTime = LocalDateTime.now();
        Long pid = null;

        List<String> commandList = buildOsCommand(info.getCommand(), info.getParameters());
        String quartzGroup = context.getJobDetail().getKey().getGroup();

        try {
            ProcessBuilder pb = new ProcessBuilder(commandList);
            pb.redirectErrorStream(true);

            process = pb.start();
            pid = process.toHandle().pid();

            // 프로세스 매니저 등록
            jobProcessManager.register(quartzGroup, info.getJobName(), process,
                    info.getTimeout(), info.getFireInstanceId());

            // 출력 읽기
            readProcessOutput(process, output);

            // 대기
            boolean finished = process.waitFor(info.getTimeout() + 5, TimeUnit.SECONDS);

            if (!finished) {
                status = ExecutionStatus.FAILURE;
                output.append("\n[System] Timeout detected.");
                process.destroyForcibly();
            } else if (process.exitValue() != 0) {
                status = ExecutionStatus.FAILURE;
                output.append("\n[System] Exit code: ").append(process.exitValue());
            }

        } catch (InterruptedException e) {
            status = ExecutionStatus.FAILURE;
            output.append("\n[System] Job interrupted.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            status = ExecutionStatus.FAILURE;
            output.append("\n[System] Error: ").append(e.getMessage());
            log.error("프로세스 실행 실패", e);
        } finally {
            if (process != null) {
                jobProcessManager.remove(quartzGroup, info.getJobName());
            }
        }

        return JobExecutionResult.builder()
                .status(status)
                .output(output.toString())
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .pid(pid)
                .build();
    }

    /**
     * 프로세스 출력 읽기
     */
    private void readProcessOutput(Process process, StringBuilder output) {
        String charset = System.getProperty("os.name").toLowerCase().contains("win")
                ? "EUC-KR" : "UTF-8";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            output.append("\n[System] Failed to read output: ").append(e.getMessage());
        }
    }

    /**
     * OS별 명령어 조립
     */
    private List<String> buildOsCommand(String command, String params) {
        List<String> cmdList = new ArrayList<>();
        String fullCmd = command + " " + (params != null ? params : "");

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            cmdList.add("cmd.exe");
            cmdList.add("/c");
        } else {
            cmdList.add("/bin/sh");
            cmdList.add("-c");
        }
        cmdList.add(fullCmd);

        return cmdList;
    }

    /**
     * 유령 스케줄 삭제
     */
    private void deleteOrphanSchedule(JobExecutionContext context) {
        try {
            boolean deleted = context.getScheduler().deleteJob(context.getJobDetail().getKey());
            if (deleted) {
                log.info("유령 스케줄 삭제 완료: {}", context.getJobDetail().getKey());
            }
        } catch (SchedulerException e) {
            log.error("유령 스케줄 삭제 실패", e);
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        this.interrupted = true;
        log.info("Job 인터럽트 요청됨");
    }
}
