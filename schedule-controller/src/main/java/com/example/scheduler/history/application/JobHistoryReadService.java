package com.example.scheduler.history.application;

import com.example.scheduler.history.api.dto.JobHistoryResponse;
import com.example.scheduler.history.infra.JobHistoryEntity;
import com.example.scheduler.history.infra.JobHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// CQS 패턴(Command Query Separation)
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // [중요] 조회 성능 최적화
public class JobHistoryReadService {

    private final JobHistoryJpaRepository repository;

    /**
     * 이력 조회 (조건부)
     * - jobName이 있으면 해당 Job만 조회
     * - 없으면 전체 조회
     */
    public Page<JobHistoryResponse> searchHistories(String scheduleGroup, String scheduleName, Pageable pageable) {
        Page<JobHistoryEntity> entities;

        if (scheduleGroup != null && !scheduleGroup.isBlank() && scheduleName != null && !scheduleName.isBlank()) {
            // 특정 Job 조회
            entities = repository.findByScheduleGroupAndScheduleName(scheduleGroup, scheduleName, pageable);
        } else {
            // 전체 조회
            entities = repository.findAll(pageable);
        }

        // Entity -> Domain -> DTO 변환
        return entities.map(entity -> JobHistoryResponse.from(entity.toDomain()));
    }
}