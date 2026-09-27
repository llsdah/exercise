package com.example.scheduler.job.infra.executor;

import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// 수정: 같은 Job의 여러 Attempt를 개별 추적하여 종료/정리가 다른 실행을 덮어쓰지 않게 한다.
@Slf4j
@Component
public class JobProcessManager {
    public record ProcessKey(String jobGroup, String jobName, String attemptId) { }

    @Getter
    @RequiredArgsConstructor
    public static class RunningJobInfo {
        private final Process process;
        private final long startTime;
        private final long timeoutSeconds;
        private final String attemptId;
        private final Map<String, String> logContext = org.slf4j.MDC.getCopyOfContextMap();
        private volatile boolean terminationRequested;
    }

    private final Map<ProcessKey, RunningJobInfo> runningProcesses = new ConcurrentHashMap<>();

    public void register(String group, String name, Process process, long timeoutSeconds, String attemptId) {
        var previous = runningProcesses.putIfAbsent(new ProcessKey(group, name, attemptId),
                new RunningJobInfo(process, System.currentTimeMillis(), timeoutSeconds, attemptId));
        if (previous != null) throw new IllegalStateException("Attempt process already registered");
    }

    public void remove(String group, String name, String attemptId) {
        runningProcesses.remove(new ProcessKey(group, name, attemptId));
    }

    public boolean terminationRequested(ProcessKey key) {
        RunningJobInfo info = runningProcesses.get(key);
        return info != null && info.terminationRequested;
    }

    public boolean killJob(String tenant, String group, String name) {
        String fullGroup = ScheduleKeyPolicy.jobGroup(tenant, group);
        boolean killed = false;
        for (ProcessKey key : runningProcesses.keySet()) {
            if (key.jobGroup().equals(fullGroup) && key.jobName().equals(name)) killed |= killProcess(key);
        }
        return killed;
    }

    public boolean killProcess(ProcessKey key) {
        RunningJobInfo info = runningProcesses.get(key);
        if (info == null) return false;
        try (var trace = com.example.scheduler.global.logging.ExecutionLogContext.restore(info.logContext)) {
        info.terminationRequested = true;
        Process process = info.process;
        // 수정: 자식 핸들을 먼저 확보한 후 종료 요청/강제 종료/생존 확인을 수행한다.
        var descendants = process.toHandle().descendants().toList();
        try {
            descendants.forEach(ProcessHandle::destroy);
            process.destroy();
            process.waitFor(1, TimeUnit.SECONDS);
            descendants.stream().filter(ProcessHandle::isAlive).forEach(ProcessHandle::destroyForcibly);
            if (process.isAlive()) process.destroyForcibly();
            process.waitFor(1, TimeUnit.SECONDS);
            return !process.isAlive() && descendants.stream().noneMatch(ProcessHandle::isAlive);
        } catch (InterruptedException failure) {
            descendants.stream().filter(ProcessHandle::isAlive).forEach(ProcessHandle::destroyForcibly);
            if (process.isAlive()) process.destroyForcibly();
            Thread.currentThread().interrupt();
            return false;
        } catch (RuntimeException failure) {
            log.warn("Process termination failed for {}", key, failure);
            return false;
        }
    }

    }

    public Map<ProcessKey, RunningJobInfo> getRunningProcesses() { return Map.copyOf(runningProcesses); }
}
