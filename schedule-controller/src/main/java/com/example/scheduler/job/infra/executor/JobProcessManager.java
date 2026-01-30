package com.example.scheduler.job.infra.executor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JobProcessManager {


    public record ProcessKey(String jobGroup, String jobName) {}

    @Getter
    @AllArgsConstructor
    public static class RunningJobInfo {
        private final Process process;
        private final long startTime;
        private final long timeoutSeconds;
    }

    private final Map<ProcessKey, RunningJobInfo> runningProcesses = new ConcurrentHashMap<>();

    // =========================================================
    // 1. 프로세스 등록
    // =========================================================
    public void register(String jobGroup, String jobName, Process process, long timeoutSeconds) {
        ProcessKey key = new ProcessKey(jobGroup, jobName);
        runningProcesses.put(key, new RunningJobInfo(process, System.currentTimeMillis(), timeoutSeconds));
        log.debug("Registered process for job: [{}]{}", jobGroup, jobName);
    }

    // =========================================================
    // 2. 프로세스 제거 (정상 종료 시)
    // =========================================================
    public void remove(String jobGroup, String jobName) {
        ProcessKey key = new ProcessKey(jobGroup, jobName);
        runningProcesses.remove(key);
    }

    // =========================================================
    // 3. 프로세스 강제 종료 (Kill)
    // =========================================================
    public boolean killJob(String jobGroup, String jobName) {
        ProcessKey key = new ProcessKey(jobGroup, jobName);

        // 1. 실행 중인 정보 조회
        RunningJobInfo info = runningProcesses.get(key);
        if (info == null) {
            log.warn("Cannot kill job. No running process found for: Group {}, Name {}", jobGroup, jobName);
            return false;
        }

        Process process = info.getProcess();
        if (process != null && process.isAlive()) {
            try {
                long pid = process.pid();
                log.info("Killing process [PID: {}] for Job: [{}]{}", pid, jobGroup, jobName);

                // [Java 9+] 자식 프로세스(Tree)까지 싹 다 정리
                // 쉘 스크립트 내부에서 또 다른 명령어를 실행했을 때 좀비 프로세스 방지
                process.toHandle().descendants().forEach(handle -> {
                    log.debug("Killing descendant process [PID: {}]", handle.pid());
                    handle.destroyForcibly();
                });

                // 본체 프로세스 종료
                process.destroyForcibly();

                log.info("Successfully killed process tree for job: [{}]{}", jobGroup, jobName);
            } catch (Exception e) {
                log.error("Failed to kill job: [{}]{}", jobGroup, jobName, e);
                return false; // 실패 처리
            }
        }

        // 2. 맵에서 제거
        remove(jobGroup, jobName);
        return true;
    }

    // 모니터링용 (전체 목록 조회)
    public Map<ProcessKey, RunningJobInfo> getRunningProcesses() {
        return runningProcesses;
    }
}