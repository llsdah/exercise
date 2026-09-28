package com.example.scheduler.retry.domain;
import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.lease.domain.LeaseClaim;
@FunctionalInterface
public interface RetryExecutor {
    void execute(LogicalExecution execution, LeaseClaim claim, com.example.scheduler.resource.application.capacity.NodeExecutionCapacity.Slot slot);
}
