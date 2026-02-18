package com.example.scheduler.history.infra.persistent;

import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.history.domain.JobExecutionHistoryRepository;
import com.example.scheduler.history.domain.JobExecutionHistorySearchCondition;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// 2. Repository Impl
@Repository
@RequiredArgsConstructor
public class JobExecutionHistoryRepositoryImpl implements JobExecutionHistoryRepository {

    private final JobExecutionHistoryJpaRepository jpaRepository;

    @Override
    public void save(JobExecutionHistory history) {
        jpaRepository.save(JobExecutionHistoryEntity.from(history));
    }


    @Override
    public long findMaxExecutionCount(String tenantId, String scheduleGroup, String scheduleName) {
        Long maxCount = jpaRepository.findMaxExecutionCount(tenantId, scheduleGroup, scheduleName);
        // 데이터가 하나도 없을 경우 0을 반환하도록 처리
        return maxCount != null ? maxCount : 0L;
    }

    @Override
    public Page<JobExecutionHistory> findByConditions(JobExecutionHistorySearchCondition cond, Pageable pageable) {
        Specification<JobExecutionHistoryEntity> spec = Specification.allOf(handlerTenantId(cond.tenantId()))
                .and(handlerScheduleGroup(cond.scheduleGroup()))
                .and(handlerScheduleName(cond.scheduleName()))
                .and(handlerFireInstanceId(cond.fireInstanceId()))
                .and(handlerStatus(cond.status()))
                .and(handlerStartTime(cond.from(), cond.to()));

        return jpaRepository.findAll(spec, pageable).map(JobExecutionHistoryEntity::toDomain);
    }

    // 1. Tenant ID (정확히 일치)
    private Specification<JobExecutionHistoryEntity> handlerTenantId(String tenantId) {
        return (root, query, cb) -> StringUtils.hasText(tenantId) ?
                cb.equal(root.get("tenantId"), tenantId) : null;
    }

    // 2. Schedule Group (정확히 일치)
    private Specification<JobExecutionHistoryEntity> handlerScheduleGroup(String group) {
        return (root, query, cb) -> StringUtils.hasText(group) ?
                cb.equal(root.get("scheduleGroup"), group) : null;
    }

    // 3. Schedule Name (부분 일치 LIKE)
    private Specification<JobExecutionHistoryEntity> handlerScheduleName(String name) {
        return (root, query, cb) -> StringUtils.hasText(name) ?
                cb.like(root.get("scheduleName"), "%" + name + "%") : null;
    }

    // 4. fireInstanceId (부분 일치 LIKE)
    private Specification<JobExecutionHistoryEntity> handlerFireInstanceId(String name) {
        return (root, query, cb) -> StringUtils.hasText(name) ?
                cb.like(root.get("fireInstanceId"), "%" + name + "%") : null;
    }

    // 5. Status (정확히 일치)
    private Specification<JobExecutionHistoryEntity> handlerStatus(ExecutionStatus status) {
        return (root, query, cb) -> status != null ?
                cb.equal(root.get("status"), status) : null;
    }

    // 6. StartTime 기간 검색 (from/to 개별 대응)
    private Specification<JobExecutionHistoryEntity> handlerStartTime(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), from));
            }

            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), to));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}