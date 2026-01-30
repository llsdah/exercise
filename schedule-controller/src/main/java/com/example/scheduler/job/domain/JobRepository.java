package com.example.scheduler.job.domain;

import java.util.Optional;

public interface JobRepository {
    Job save(Job job);
    void deleteByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);
    Optional<Job> findByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);
    boolean existsByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);
}