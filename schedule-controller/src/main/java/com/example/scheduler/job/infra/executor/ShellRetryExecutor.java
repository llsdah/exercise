package com.example.scheduler.job.infra.executor;
import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.history.application.HistoryExecutionCoordinator;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.lease.infra.watchdog.ExecutionLeaseMonitor;
import com.example.scheduler.retry.domain.RetryExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ShellRetryExecutor implements RetryExecutor {
    private final JobProcessManager processes;
    private final HistoryExecutionCoordinator coordinator;
    private final ExecutionLeaseMonitor monitor;
    private final SchedulerProperties properties;
    private final com.example.scheduler.resource.application.NodeExecutionCapacity capacity;
    @Override
    public void execute(JobExecutionHistory execution, LeaseClaim claim, com.example.scheduler.resource.application.NodeExecutionCapacity.Slot slot) {
        // Each retry owns its interrupt flag; use the same process/heartbeat/fencing path as Quartz.
        new ShellCommandJob(processes, coordinator, monitor, properties, capacity).executeRetry(execution, claim, slot);
    }
}
