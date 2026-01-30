package com.example.scheduler.job.infra.persistent;

import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {
    
    private final JobJpaRepository jpaRepository; // Spring Data JPA Interface

    @Override
    public Job save(Job job) {
        return jpaRepository.save(JobEntity.from(job)).toDomain();
    }

    @Override
    public void deleteByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName) {
        jpaRepository.deleteByScheduleGroupAndScheduleName(scheduleGroup, scheduleName);
    }

    @Override
    public Optional<Job> findByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName) {
        return jpaRepository.findByScheduleGroupAndScheduleName(scheduleGroup, scheduleName).map(JobEntity::toDomain);
    }

    @Override
    public boolean existsByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName) {
        return jpaRepository.existsByScheduleGroupAndScheduleName(scheduleGroup, scheduleName);
    }
}