package com.example.scheduler.history.infra;

import com.example.scheduler.history.domain.JobExecutionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface JobHistoryJpaRepository extends JpaRepository<JobHistoryEntity, Long> {
    void save(JobExecutionHistory history);

    // [중요] 그룹과 이름이 모두 같아야 동일 작업의 이력입니다.
    long countByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName);

    // 1. 특정 Job의 이력 조회 (페이징)
    // 인덱스(idx_history_group_name)를 타게 됨
    Page<JobHistoryEntity> findByScheduleGroupAndScheduleName(String scheduleGroup, String scheduleName, Pageable pageable);

    // 2. 전체 이력 조회는 기본 findAll(Pageable) 사용
}


