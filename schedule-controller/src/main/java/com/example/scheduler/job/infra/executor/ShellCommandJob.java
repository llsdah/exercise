package com.example.scheduler.job.infra.executor;

import com.example.scheduler.global.util.SystemMonitorService;
import com.example.scheduler.history.application.JobHistoryCommand;
import com.example.scheduler.history.application.JobHistoryService; // 타 도메인 서비스 호출
import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShellCommandJob implements Job {

    private final JobProcessManager jobProcessManager;
    private final JobHistoryService jobHistoryService; // History 서비스 주입
    private final SystemMonitorService systemMonitorService;

    @Override
    public void execute(JobExecutionContext context) {

        // 1. Quartz Context에서 정보 추출 (Group 필수!)
        String jobGroup = context.getJobDetail().getKey().getGroup();
        String jobName = context.getJobDetail().getKey().getName();
        JobDataMap data = context.getJobDetail().getJobDataMap();
        //Long jobId = data.getLong("jobId");
        String command = data.getString("command");
        String params = data.getString("parameters");
        String jobType = data.getString("jobType");
        String scheduleType = data.getString("scheduleType");
        long timeout = data.containsKey("timeout") ? data.getLong("timeout") : 3600; // 기본 1시간
        Process process = null;
        StringBuilder output = new StringBuilder();
        String exitCode = ExecutionStatus.WARNING.name();
        Long pid = null;
        LocalDateTime startTime = LocalDateTime.now();
        List<String> processMap = getOsCommand(command, params);

        log.info("TYPE : {}, schedule : {}",jobType,scheduleType);

        // [변경] Command 객체 생성 (빌더 패턴으로 가독성 확보)
        JobHistoryCommand historyCommand = JobHistoryCommand.builder()
                .jobGroup(jobGroup)
                .jobName(jobName)
                .scheduleType(scheduleType)
                .jobType(jobType)
                .scheduleType(scheduleType)
                .startTime(startTime)
                // .endTime()
                .fullCommand(String.join( " ", processMap))
                .status(ExecutionStatus.valueOf(exitCode))
                .message(output.toString())
                .build();

        log.info("commend : {}", String.join( " ", processMap));

        // 1. 시스템 부하 체크 (OSHI)
        if (systemMonitorService.isSystemOverloaded()) {
            log.warn("Job [{}] skipped due to system overload.", jobName);
            // exitcode = SKIPPED
            output.append("System Overload Detected");
            jobHistoryService.recordHistory(historyCommand);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(processMap);
            pb.redirectErrorStream(true);

            startTime = LocalDateTime.now();
            process = pb.start();
            pid = process.toHandle().pid(); // PID 확보

            // 2. 프로세스 매니저 등록
            jobProcessManager.register(jobGroup, jobName, process, timeout);

            // 3. 로그 읽기
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),
                    System.getProperty("os.name").toLowerCase().contains("win") ? "EUC-KR" : "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    //log.info("output line : {}",line);
                }
            }

            // 4. 대기
            boolean finished = process.waitFor(timeout + 5, TimeUnit.SECONDS);
            if (finished) {
                //exitCode = String.valueOf(process.exitValue());
                exitCode = ExecutionStatus.SUCCESS.name();
            } else {
                // exitCode = "TIMEOUT";
                exitCode = ExecutionStatus.FAILURE.name();
                output.append("\n[System] Timeout detected by executor.");
                process.destroyForcibly();
            }

        } catch (InterruptedException e) {
            // exitCode = "KILLED";
            exitCode = ExecutionStatus.FAILURE.name();
            output.append("\n[System] Job killed by User or Watchdog.");
        } catch (Exception e) {
            // exitCode = "ERROR";
            exitCode = ExecutionStatus.FAILURE.name();
            output.append("\n[System] Error: ").append(e.getMessage());
            log.error("Execution failed", e);
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            historyCommand = JobHistoryCommand.builder()
                    .jobGroup(jobGroup)
                    .jobName(jobName)
                    .scheduleType(scheduleType)
                    .jobType(jobType)
                    .startTime(startTime)
                    .endTime(endTime)
                    .fullCommand(String.join( " ", processMap))
                    .status(ExecutionStatus.valueOf(exitCode))
                    .message(output.toString())
                    .build();

            if (process != null) jobProcessManager.remove(jobGroup, jobName);

            // 5. 이력 저장 (History Service 호출)
            jobHistoryService.recordHistory(historyCommand);
            log.info("output : {}", output.toString());
        }
    }

    private List<String> getOsCommand(String cmd, String params) {
        List<String> cmdList = new ArrayList<>();
        String fullCmd = cmd + " " + (params == null ? "" : params);
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            cmdList.add("cmd.exe"); cmdList.add("/c");
        } else {
            cmdList.add("/bin/sh"); cmdList.add("-c");
        }
        cmdList.add(fullCmd);
        return cmdList;
    }
}