package com.example.scheduler.job.infra.persistent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobJpaRepository extends JpaRepository<JobEntity, JobEntityId> {

}