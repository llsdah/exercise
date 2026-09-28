package com.example.scheduler.job.infra.executor;
import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.execution.application.ExecutionCoordinator;
import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.lease.infra.watchdog.ExecutionLeaseMonitor;
import com.example.scheduler.retry.domain.RetryExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ShellRetryExecutor implements RetryExecutor {
    private final JobProcessManager processes;
    private final ExecutionCoordinator coordinator;
    private final ExecutionLeaseMonitor monitor;
    private final SchedulerProperties properties;
    private final com.example.scheduler.resource.application.capacity.NodeExecutionCapacity capacity;
    @Override
    public void execute(LogicalExecution execution, LeaseClaim claim, com.example.scheduler.resource.application.capacity.NodeExecutionCapacity.Slot slot) {
        // Each retry owns its interrupt flag; use the same process/heartbeat/fencing path as Quartz.
        new ShellCommandJob(processes, coordinator, monitor, properties, capacity).executeRetry(execution, claim, slot);
    }
}
