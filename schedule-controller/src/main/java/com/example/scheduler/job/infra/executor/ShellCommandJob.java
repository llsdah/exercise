package com.example.scheduler.job.infra.executor;

// 수정: 실행 서비스/도메인/감시 어댑터를 각 계층의 패키지에서 참조한다.
import com.example.scheduler.history.application.HistoryExecutionCoordinator;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.lease.infra.watchdog.ExecutionLeaseMonitor;
import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.HistoryExecutionRequest;
import com.example.scheduler.job.application.model.JobExecution;
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

// 수정: Lease/Attempt 확정 후에만 Process를 실행하며 모든 완료 기록은 fencing 검증을 거친다.
@Slf4j
@Component
@RequiredArgsConstructor
public class ShellCommandJob implements Job, InterruptableJob {
    private final JobProcessManager jobProcessManager;
    private final HistoryExecutionCoordinator coordinator;
    private final ExecutionLeaseMonitor monitor;
    private final SchedulerProperties schedulerProperties;
    private final com.example.scheduler.resource.application.NodeExecutionCapacity capacity;
    private volatile boolean interrupted;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobExecution info = extractExecution(context);
        Instant scheduledAt = scheduledInstant(context);
        // 수정: recovery Trigger는 원래 예약 시각, 수동 요청은 고유 requestId로 논리 실행을 식별한다.
        String occurrence = context.getMergedJobDataMap().getString("executionRequestId");
        occurrence = occurrence == null ? "scheduled:" + scheduledAt.toEpochMilli() : "manual:" + occurrence;
        LeaseClaim claim;
        com.example.scheduler.resource.application.NodeExecutionCapacity.Slot slot = null;
        try {
            try {
                String id = coordinator.ensureExecution(new HistoryExecutionRequest(info.getTenantId(), info.getJobGroup(), info.getJobName(),
                        occurrence, scheduledAt, info.getFireInstanceId(), info.getScheduleType(), info.getJobType(),
                        info.getCronExpression(), info.getCommand(), info.getParameters()));
                try (var trace = com.example.scheduler.global.logging.ExecutionLogContext.open(id, null,
                        coordinator.findExecution(id).orElseThrow().getTriggerNodeId(), null, null, null)) {
                    log.info("Quartz execution resolved");
                    slot = capacity.tryAcquire().orElse(null);
                    if (slot == null) { coordinator.deferForCapacity(id); return; }
                    var acquired = coordinator.claim(id);
                    if (acquired.isEmpty()) {
                        log.info("Execution {} has no new execution right; skip process start", id);
                        return;
                    }
                    claim = acquired.get();
                }
            } catch (RuntimeException failure) {
                throw new JobExecutionException("Unable to acquire durable execution lease", failure);
            }
            executeProcess(info, context.getJobDetail().getKey().getGroup(), claim, slot);
        } finally { if (slot != null) slot.close(); }
    }

    public void executeRetry(com.example.scheduler.history.domain.JobExecutionHistory execution, LeaseClaim claim, com.example.scheduler.resource.application.NodeExecutionCapacity.Slot slot) {
        var info = JobExecution.of(execution.getTenantId(), execution.getScheduleGroup(), execution.getScheduleName(),
                execution.getFireInstanceId(), execution.getCronExpression(), execution.getCommand(), execution.getParameters(),
                execution.getJobType(), execution.getScheduleType(), schedulerProperties.timeoutSeconds(), LocalDateTime.now());
        executeProcess(info, ScheduleKeyPolicy.jobGroup(execution.getTenantId(), execution.getScheduleGroup()), claim, slot);
    }

    private Instant scheduledInstant(JobExecutionContext context) {
        if (context.isRecovering()) {
            Object original = context.getMergedJobDataMap().get(Scheduler.FAILED_JOB_ORIGINAL_TRIGGER_SCHEDULED_FIRETIME_IN_MILLISECONDS);
            if (original == null) throw new IllegalStateException("Recovery trigger is missing original scheduled time");
            return Instant.ofEpochMilli(Long.parseLong(original.toString()));
        }
        if (context.getScheduledFireTime() == null) throw new IllegalStateException("Missing scheduled fire time");
        return context.getScheduledFireTime().toInstant();
    }

    private JobExecution extractExecution(JobExecutionContext context) {
        String quartzGroup = context.getJobDetail().getKey().getGroup();
        JobDataMap data = context.getMergedJobDataMap();
        return JobExecution.of(ScheduleKeyPolicy.extractTenantId(quartzGroup), ScheduleKeyPolicy.extractGroup(quartzGroup),
                context.getJobDetail().getKey().getName(), context.getFireInstanceId(),
                data.getString("cronExpression"), data.getString("command"), data.getString("parameters"),
                data.getString("jobType"), data.getString("scheduleType"), schedulerProperties.timeoutSeconds(),
                LocalDateTime.ofInstant(scheduledInstant(context), ZoneId.systemDefault()));
    }

    private void executeProcess(JobExecution info, String quartzGroup, LeaseClaim claim, com.example.scheduler.resource.application.NodeExecutionCapacity.Slot slot) {
        coordinator.verifyLocalWorker(claim);
        capacity.requireLocalSlot(slot);
        var execution = coordinator.findExecution(claim.executionId()).orElseThrow();
        try (var trace = com.example.scheduler.global.logging.ExecutionLogContext.open(claim.executionId(),
                claim.attemptId(), execution.getTriggerNodeId(), null, claim.token(), null)) {
            runProcess(info, quartzGroup, claim, slot);
        }
    }

    private void runProcess(JobExecution info, String quartzGroup, LeaseClaim claim, com.example.scheduler.resource.application.NodeExecutionCapacity.Slot slot) {
        Process process = null;
        Thread outputReader = null;
        StringBuffer output = new StringBuffer();
        Integer exitCode = null;
        String uncertainty = null;
        boolean startInvoked = false;
        boolean startSucceeded = false;
        String startFailure = null;
        try (var guard = monitor.watch(claim)) {
            ProcessBuilder builder = createProcessBuilder(info.getCommand(), info.getParameters());
            builder.redirectErrorStream(true);
            // 수정: 배치가 논리 실행과 시도를 연계할 수 있도록 환경 변수로 식별자와 토큰을 전달한다.
            builder.environment().put("BATCH_EXECUTION_ID", claim.executionId());
            builder.environment().put("BATCH_ATTEMPT_ID", claim.attemptId());
            builder.environment().put("BATCH_LEASE_TOKEN", Long.toString(claim.token()));
            builder.environment().put("BATCH_NODE_ID", claim.nodeId());
            if (!guard.valid() || interrupted) throw new IllegalStateException("Execution right lost before process start");
            // Commit launch intent before the OS call. A crash in this gap remains inconclusive.
            coordinator.launchRequested(claim);
            if (!guard.valid() || interrupted) throw new IllegalStateException("Execution right lost before process start");
            org.slf4j.MDC.put("workerNodeId", claim.nodeId());
            guard.captureContext();
            log.info("Invoking ProcessBuilder.start");
            startInvoked = true;
            process = slot.start(builder);
            startSucceeded = true;
            org.slf4j.MDC.put("pid", Long.toString(process.pid()));
            guard.captureContext();
            log.info("Process started");
            jobProcessManager.register(quartzGroup, info.getJobName(), process, info.getTimeout(), claim.attemptId());
            coordinator.started(claim, process.pid(), process.info().startInstant().orElse(Instant.now()));
            Process running = process;
            // 수정: 출력 소비가 timeout/heartbeat 검사를 막지 않도록 별도 가상 스레드에서 읽는다.
            outputReader = Thread.ofVirtual().start(com.example.scheduler.global.logging.ExecutionLogContext.propagate(() -> readProcessOutput(running, output)));
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(info.getTimeout());
            while (!process.waitFor(200, TimeUnit.MILLISECONDS)) {
                if (!guard.valid() || interrupted || System.nanoTime() >= deadline) {
                    uncertainty = !guard.valid() ? "Lease lost" : interrupted ? "Execution interrupted" : "Execution timeout";
                    break;
                }
            }
            if (!guard.valid()) uncertainty = "Lease lost";
            if (interrupted) uncertainty = "Execution interrupted";
            if (uncertainty == null) exitCode = process.exitValue();
        } catch (Exception failure) {
            // An exception thrown by start() did not return a process; do not invent an OS exit code.
            if (startSucceeded) uncertainty = failure.toString();
            else startFailure = (startInvoked ? "ProcessBuilder.start() failed: " : "Process was not started: ") + failure;
            output.append("\n[System] ").append(failure);
            if (failure instanceof InterruptedException) Thread.currentThread().interrupt();
        } finally {
            if (process != null) {
                var processKey = new JobProcessManager.ProcessKey(quartzGroup, info.getJobName(), claim.attemptId());
                if (jobProcessManager.terminationRequested(processKey)) uncertainty = "Process termination requested";
                if (process.isAlive()) jobProcessManager.killProcess(processKey);
                // Registration itself may have failed; do not lose the process handle.
                if (process.isAlive() && !jobProcessManager.getRunningProcesses().containsKey(processKey)) {
                    process.toHandle().descendants().forEach(ProcessHandle::destroyForcibly);
                    process.destroyForcibly();
                }
                if (process.isAlive()) uncertainty = "Process termination was not confirmed";
                if (!process.isAlive()) {
                    exitCode = process.exitValue();
                    jobProcessManager.remove(quartzGroup, info.getJobName(), claim.attemptId());
                }
            }
            slot.close();
            if (outputReader != null) {
                try { outputReader.join(2000); }
                catch (InterruptedException interruptedException) { Thread.currentThread().interrupt(); }
                // 수정: 자식이 출력 핸들을 계속 보유해도 읽기 스레드가 남지 않도록 종료한다.
                if (outputReader.isAlive()) {
                    outputReader.interrupt();
                    try { process.getInputStream().close(); }
                    catch (java.io.IOException failure) { log.debug("Output stream already closed", failure); }
                }
            }
        }
        try {
            if (!startSucceeded) coordinator.startFailed(claim, startFailure == null ? "Process was not started" : startFailure, output.toString(), startInvoked);
            else coordinator.finish(claim, exitCode, uncertainty, output.toString());
            log.info("Process outcome recorded: exitCode={}, reason={}", exitCode, startSucceeded ? uncertainty : startFailure);
        } catch (RuntimeException failure) {
            // 수정: stale 실행자는 기존 이력도 쓰지 못한다. 미확정 결과는 Lease 스캔이 UNKNOWN 처리한다.
            log.warn("Attempt {} could not record a fenced result", claim.attemptId(), failure);
            return;
        }
    }

    private void readProcessOutput(Process process, StringBuffer output) {
        String charset = System.getProperty("os.name").toLowerCase().contains("win") ? "EUC-KR" : "UTF-8";
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
            char[] buffer = new char[2048];
            int count;
            while ((count = reader.read(buffer)) >= 0) {
                // 수정: 로그 보관량을 제한하되 파이프는 계속 소비해 Process가 막히지 않게 한다.
                int remaining = 65536 - output.length();
                if (remaining > 0) output.append(buffer, 0, Math.min(remaining, count));
            }
        } catch (Exception failure) {
            log.debug("Process output closed", failure);
        }
    }

    private List<String> buildOsCommand(String command, String params) {
        String full = command + " " + (params == null ? "" : params);
        return System.getProperty("os.name").toLowerCase().contains("win")
                ? List.of("cmd.exe", "/c", full) : List.of("/bin/sh", "-c", full);
    }

    protected ProcessBuilder createProcessBuilder(String command, String parameters) {
        return new ProcessBuilder(buildOsCommand(command, parameters));
    }

    @Override
    public void interrupt() {
        // 수정: 실행 루프가 interrupt를 감지하고 해당 Attempt만 종료한다.
        interrupted = true;
    }
}
