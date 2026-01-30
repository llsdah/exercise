package com.example.scheduler.history.infra;

import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.history.domain.JobHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobHistoryRepositoryImpl implements JobHistoryRepository {

    private final JobHistoryJpaRepository jpaRepository; // Spring Data JPA

    @Override
    public void save(JobExecutionHistory history) {
        jpaRepository.save(JobHistoryEntity.from(history));
    }


    @Override
    public long countByScheduleGroupAndScheduleName(String jobGroup, String jobName) {
        // JPA 리포지토리의 기능을 그대로 위임
        return jpaRepository.countByScheduleGroupAndScheduleName(jobGroup, jobName);
    }

    // (선택 사항) 특정 작업의 이력을 조회하는 기능도 필요하다면 아래처럼 수정
    /**
    @Override
    public List<JobExecutionHistory> findByJobGroupAndJobName(String scheduleGroup, String scheduleName) {
        return jpaRepository.findByJobGroupAndJobNameOrderByStartTimeDesc(scheduleGroup, scheduleName)
                .stream()
                .map(JobHistoryEntity::toDomain)
                .toList();
    }
    */

}