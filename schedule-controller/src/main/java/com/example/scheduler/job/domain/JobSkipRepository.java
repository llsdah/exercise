package com.example.scheduler.job.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JobSkipRepository {
    JobSkip save(JobSkip jobSkip);
    Optional<JobSkip> findJobSkip(String tenantId, String scheduleGroup, String scheduleName, LocalDateTime targetTime);
    void delete(String tenantId, String scheduleGroup, String scheduleName, LocalDateTime targetTime);
    Page<JobSkip> findByConditions(JobSkipSearchCondition condition, Pageable pageable);
    // [추가] 현재 등록된 스킵 개수 조회
    long countByJob(String tenantId, String scheduleGroup, String scheduleName);
}