package com.example.scheduler.history.infra.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 1. JPA Interface
// ID 타입 변경: Long -> JobHistoryId
interface JobExecutionHistoryJpaRepository extends JpaRepository<JobExecutionHistoryEntity, JobExecutionHistoryId>, JpaSpecificationExecutor<JobExecutionHistoryEntity> {


    @Query("SELECT MAX(e.executionCount) FROM JobExecutionHistoryEntity e " +
            "WHERE e.tenantId = :tenantId " +
            "AND e.scheduleGroup = :scheduleGroup " +
            "AND e.scheduleName = :scheduleName")
    Long findMaxExecutionCount(
            @Param("tenantId") String tenantId,
            @Param("scheduleGroup") String scheduleGroup,
            @Param("scheduleName") String scheduleName
    );
}

