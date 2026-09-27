package com.example.scheduler.retry.domain;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.lease.domain.LeaseClaim;
@FunctionalInterface
public interface RetryExecutor {
    void execute(JobExecutionHistory execution, LeaseClaim claim, com.example.scheduler.resource.application.NodeExecutionCapacity.Slot slot);
}
