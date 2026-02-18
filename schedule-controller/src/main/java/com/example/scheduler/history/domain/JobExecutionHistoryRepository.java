package com.example.scheduler.history.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobExecutionHistoryRepository {
    void save(JobExecutionHistory history);

    Page<JobExecutionHistory> findByConditions(JobExecutionHistorySearchCondition condition, Pageable pageable);

    long findMaxExecutionCount(String tenantId, String scheduleGroup, String scheduleName);
}