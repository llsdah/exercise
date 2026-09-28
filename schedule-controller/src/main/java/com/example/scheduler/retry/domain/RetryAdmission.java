package com.example.scheduler.retry.domain;
import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.lease.domain.LeaseClaim;
import java.time.Instant;
/** A non-null claim authorizes one new attempt; snapshot is captured under the same row lock. */
public record RetryAdmission(LeaseClaim claim, LogicalExecution execution, Instant nextRetryAt, String reason) {}
