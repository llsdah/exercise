package com.example.scheduler.job.infra.persistent;

import com.example.scheduler.job.domain.JobSkipSearchCondition;
import com.example.scheduler.job.domain.JobSkip;
import com.example.scheduler.job.domain.JobSkipRepository;
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
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class JobSkipRepositoryImpl implements JobSkipRepository {

    private final JobSkipJpaRepository jobSkipJpaRepository;

    @Override
    public JobSkip save(JobSkip domain) {
        JobSkipEntity entity = toEntity(domain);
        JobSkipEntity saved = jobSkipJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<JobSkip> findJobSkip(String tenantId, String scheduleGroup, String scheduleName, LocalDateTime skipTime) {

        JobSkipId id = new JobSkipId(tenantId, scheduleGroup, scheduleName, skipTime);
        return jobSkipJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void delete(String tenantId, String scheduleGroup, String scheduleName, LocalDateTime skipTime) {
        JobSkipId id = new JobSkipId(tenantId, scheduleGroup, scheduleName, skipTime);
        jobSkipJpaRepository.deleteById(id);
    }

    @Override
    public long countByJob(String tenantId, String scheduleGroup, String scheduleName) {
        return jobSkipJpaRepository.countByTenantIdAndScheduleGroupAndScheduleName(
                tenantId, scheduleGroup, scheduleName
        );
    }

    @Override
    public Page<JobSkip> findByConditions(JobSkipSearchCondition cond, Pageable pageable) {
        Specification<JobSkipEntity> spec = Specification.allOf(handlerTenantId(cond.tenantId()))
                .and(handlerScheduleGroup(cond.scheduleGroup()))
                .and(handlerScheduleName(cond.scheduleName()))
                .and(handlerSkipTime(cond.from(), cond.to()));

        return jobSkipJpaRepository.findAll(spec, pageable).map(this::toDomain);
    }

    // 조건별 Specification 생성 (람다 활용)
    private Specification<JobSkipEntity> handlerTenantId(String tenantId) {
        return (root, query, cb) -> StringUtils.hasText(tenantId) ? cb.equal(root.get("tenantId"), tenantId) : null;
    }

    private Specification<JobSkipEntity> handlerScheduleGroup(String group) {
        return (root, query, cb) -> StringUtils.hasText(group) ? cb.like(root.get("scheduleGroup"), "%" + group + "%") : null;
    }

    private Specification<JobSkipEntity> handlerScheduleName(String name) {
        return (root, query, cb) -> StringUtils.hasText(name) ? cb.like(root.get("scheduleName"), "%" + name + "%") : null;
    }

    private Specification<JobSkipEntity> handlerSkipTime(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // from만 있을 때: skipTime >= from
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("skipTime"), from));
            }

            // to만 있을 때: skipTime <= to
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("skipTime"), to));
            }

            // 둘 다 없으면 predicates가 비어있으므로 null 반환 (조건 무시)
            // 둘 다 있거나 하나만 있으면 AND로 묶어서 반환
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // --- Mappers ---
    private JobSkipEntity toEntity(JobSkip domain) {
        return JobSkipEntity.builder()
                .tenantId(domain.getTenantId())
                .scheduleGroup(domain.getScheduleGroup())
                .scheduleName(domain.getScheduleName())
                .skipTime(domain.getSkipTime())
                .regUserId(domain.getRegUserId() != null ? domain.getRegUserId() : "SYSTEM")
                .build();
    }

    private JobSkip toDomain(JobSkipEntity entity) {
        return JobSkip.builder()
                .tenantId(entity.getTenantId())
                .scheduleGroup(entity.getScheduleGroup())
                .scheduleName(entity.getScheduleName())
                .skipTime(entity.getSkipTime())
                .regUserId(entity.getRegUserId())
                .build();
    }
}