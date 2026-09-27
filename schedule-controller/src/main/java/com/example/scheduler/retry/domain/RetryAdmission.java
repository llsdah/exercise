package com.example.scheduler.retry.domain;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.lease.domain.LeaseClaim;
import java.time.Instant;
/** A non-null claim authorizes one new attempt; snapshot is captured under the same row lock. */
public record RetryAdmission(LeaseClaim claim, JobExecutionHistory execution, Instant nextRetryAt, String reason) {}
