package com.example.scheduler.job.application.event;

import com.example.scheduler.job.application.model.JobExecution;

public record JobExecutionCompletedEvent(JobExecution execution) {}
