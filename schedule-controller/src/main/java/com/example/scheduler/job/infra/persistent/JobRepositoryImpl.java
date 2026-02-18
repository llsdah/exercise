package com.example.scheduler.job.infra.persistent;

import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final JobJpaRepository jpaRepository;

    @Override
    public Job save(Job job) {
        // Job 도메인을 Entity로 변환하여 저장 -> 다시 도메인으로 변환하여 반환
        return jpaRepository.save(JobEntity.from(job)).toDomain();
    }

    @Override
    public void delete(String tenantId, String jobGroup, String jobName) {
        // [변경] 복합키 객체 생성 후 삭제
        jpaRepository.deleteById(new JobEntityId(tenantId, jobGroup, jobName));
    }

    @Override
    public Optional<Job> findJob(String tenantId, String jobGroup, String jobName) {
        // [변경] 복합키 객체 생성 후 조회 (JPA 표준 findById 사용)
        return jpaRepository.findById(new JobEntityId(tenantId, jobGroup, jobName))
                .map(JobEntity::toDomain);
    }

    @Override
    public boolean exists(String tenantId, String jobGroup, String jobName) {
        // [변경] 복합키 객체 생성 후 존재 여부 확인
        return jpaRepository.existsById(new JobEntityId(tenantId, jobGroup, jobName));
    }


}