package com.example.scheduler.job.infra.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface JobSkipRepository extends JpaRepository<JobSkipEntity, Long> {

    Optional<JobSkipEntity> findByJobGroupIgnoreCaseAndJobNameIgnoreCase(
            String jobGroup,
            String jobName
    );
}