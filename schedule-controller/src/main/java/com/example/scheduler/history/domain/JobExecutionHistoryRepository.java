package com.example.scheduler.history.domain;

import com.example.scheduler.execution.domain.LogicalExecution;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobExecutionHistoryRepository {
    void save(LogicalExecution history);

    Page<LogicalExecution> findByConditions(JobExecutionHistorySearchCondition condition, Pageable pageable);

    long findMaxExecutionCount(String tenantId, String scheduleGroup, String scheduleName);
}