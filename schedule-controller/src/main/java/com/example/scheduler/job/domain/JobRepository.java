package com.example.scheduler.job.domain;

import java.util.List;
import java.util.Optional;

public interface JobRepository {

    // 저장 및 수정 (Entity의 save와 동일)
    Job save(Job job);

    // [변경] 식별자가 복합키(Tenant + Group + Name)이므로 3개 모두 필수
    void delete(String tenantId, String jobGroup, String jobName);

    // [변경] 단건 조회 시에도 3개의 키 필요
    Optional<Job> findJob(String tenantId, String jobGroup, String jobName);

    // [변경] 존재 여부 확인
    boolean exists(String tenantId, String jobGroup, String jobName);


}