package com.example.scheduler.job.infra.executor;

import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
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
        private final String fireInstanceId; // [추가] Quartz 실행 고유 ID
    }

    private final Map<ProcessKey, RunningJobInfo> runningProcesses = new ConcurrentHashMap<>();

    // =========================================================
    // 1. 프로세스 등록 (fireInstanceId 파라미터 추가)
    // =========================================================
    public void register(String jobGroup, String jobName, Process process, long timeoutSeconds, String fireInstanceId) {
        ProcessKey key = new ProcessKey(jobGroup, jobName);
        // 생성자에 fireInstanceId 추가
        runningProcesses.put(key, new RunningJobInfo(process, System.currentTimeMillis(), timeoutSeconds, fireInstanceId));
        log.debug("Registered process for job: [{}]{} (InstanceId: {})", jobGroup, jobName, fireInstanceId);
    }

    // =========================================================
    // 2. 프로세스 제거 (기존과 동일)
    // =========================================================
    public void remove(String jobGroup, String jobName) {
        ProcessKey key = new ProcessKey(jobGroup, jobName);
        runningProcesses.remove(key);
    }

    // =========================================================
    // 3. 프로세스 정보 조회 (Watchdog에서 사용)
    // =========================================================
    public RunningJobInfo getJobInfo(String jobGroup, String jobName) {
        return runningProcesses.get(new ProcessKey(jobGroup, jobName));
    }

    // =========================================================
    // 4. 프로세스 강제 종료 (Kill)
    // =========================================================
    public boolean killJob(String tenantId, String jobGroup, String jobName) {
        String quartzGroup = ScheduleKeyPolicy.jobGroup(tenantId, jobGroup);
        ProcessKey key = new ProcessKey(quartzGroup, jobName);

        RunningJobInfo info = runningProcesses.get(key);
        if (info == null) {
            log.warn("Cannot kill job. No running process found for: [{}]{}", quartzGroup, jobName);
            log.error("--- Kill Job Failed: Key not found ---");
            log.error("Searching for -> Group: [{}], Name: [{}]", quartzGroup, jobName);
            log.error("Searching Key HashCode: {}", key.hashCode());

            // 현재 Map에 들어있는 모든 키를 출력해서 눈으로 비교
            runningProcesses.keySet().forEach(k -> {
                log.info("Existing Key -> Group: [{}], Name: [{}], HashCode: {}",
                        k.jobGroup(), k.jobName(), k.hashCode());

                // 문자열 비교 (공백이나 숨은 특수문자 확인)
                if (k.jobName().trim().equals(jobName.trim())) {
                    log.warn("Found similar jobName! Check for invisible characters or case sensitivity.");
                }
            });

            return false;
        }

        Process process = info.getProcess();
        if (process != null && process.isAlive()) {
            try {
                // 자식 프로세스까지 깔끔하게 정리 (Shell 실행 시 필수)
                process.toHandle().descendants().forEach(handle -> {
                    log.debug("Killing descendant process [PID: {}]", handle.pid());
                    handle.destroyForcibly();
                });
                process.destroyForcibly();
                log.info("Successfully killed process tree for job: [{}]{}", quartzGroup, jobName);
            } catch (Exception e) {
                log.error("Failed to kill process for job: [{}]{}", quartzGroup, jobName, e);
                return false;
            }
        }

        remove(quartzGroup, jobName);
        return true;
    }

    public Map<ProcessKey, RunningJobInfo> getRunningProcesses() {
        return runningProcesses;
    }
}