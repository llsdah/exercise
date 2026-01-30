package com.example.scheduler.job.infra.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface JobJpaRepository extends JpaRepository<JobEntity, Long> {

    Optional<JobEntity> findByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);

    void deleteByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);

    boolean existsByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);
}