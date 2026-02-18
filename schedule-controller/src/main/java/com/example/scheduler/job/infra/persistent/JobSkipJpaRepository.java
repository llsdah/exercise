package com.example.scheduler.job.infra.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface JobSkipJpaRepository extends JpaRepository<JobSkipEntity, JobSkipId>, JpaSpecificationExecutor<JobSkipEntity> {

    long countByTenantIdAndScheduleGroupAndScheduleName(
            String tenantId,
            String scheduleGroup,
            String scheduleName
    );

}