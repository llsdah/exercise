package com.example.scheduler.history.application;

import com.example.scheduler.history.api.dto.JobExecutionHistoryResponse;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.history.domain.JobExecutionHistoryRepository;

import com.example.scheduler.history.domain.JobExecutionHistorySearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// CQS 패턴(Command Query Separation)
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // [중요] 조회 성능 최적화
public class JobExecutionHistoryReadService {


    private final JobExecutionHistoryRepository repository;

    /**
     * 이력 통합 검색
     * - TenantId, Group, Name, Status, Period(From~To) 조건을 조합하여 조회
     */
    public Page<JobExecutionHistory> searchHistories(JobExecutionHistorySearchCondition condition, Pageable pageable) {

        return repository.findByConditions(condition, pageable);

    }
}