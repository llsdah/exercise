package com.example.scheduler.history.domain;

public interface JobHistoryRepository {
    void save(JobExecutionHistory history);

    long countByScheduleGroupAndScheduleName(String jobGroup, String jobName);
}