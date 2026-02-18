package com.example.scheduler.job.application;

import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.global.error.BusinessException;
import com.example.scheduler.job.api.dto.ScheduleResponse;
import com.example.scheduler.job.application.model.JobCommand;
import com.example.scheduler.job.domain.*;
import com.example.scheduler.job.infra.executor.JobProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;      // Domain Repository (Interface)
    private final JobScheduler jobScheduler;        // Quartz Scheduler Wrapper
    private final JobProcessManager jobProcessManager;
    private final SchedulerProperties properties;

    /**
     * [작업 등록/수정]
     * DB에 메타정보를 저장하고, 스케줄러에 등록합니다.
     */
    @Transactional
    public LocalDateTime registerOrUpdateJob(JobCommand command) {
        log.debug("Processing Job Command: [Tenant: {}] {}.{}", command.tenantId(), command.jobGroup(), command.jobName());

        Job jobToRegister;

        if (properties.useMetaTable()) {
            // 1. 기존 작업 조회 (TenantId + Group + Name)
            jobToRegister = jobRepository.findJob(command.tenantId(), command.jobGroup(), command.jobName())
                    .map(existingJob -> {
                        // 1-A. Update: 도메인 객체의 비즈니스 메서드 호출
                        log.info("Updating existing job: [{}]{}.{}", command.tenantId(), command.jobGroup(), command.jobName());

                        existingJob.update(
                                command.cronExpression(),
                                command.command(),
                                command.parameters(),
                                command.description(),
                                command.regUserId() // 수정자 (modifyUserId)
                        );
                        // 필요 시 상태 활성화 로직 추가 가능
                        // existingJob.activate();
                        return existingJob;
                    })
                    .orElseGet(() -> {
                        // 1-B. Create: 팩토리 메서드 호출
                        log.info("Creating new job: [{}]{}.{}", command.tenantId(), command.jobGroup(), command.jobName());

                        return Job.create(
                                command.tenantId(),
                                command.jobGroup(),
                                command.jobName(),
                                command.scheduleType(),
                                command.jobType(),
                                command.cronExpression(),
                                command.command(),
                                command.parameters(),
                                command.description(),
                                command.startTime(),
                                command.endTime(),
                                command.regUserId() // 등록자 (regUserId)
                        );
                    });

            // 2. DB 저장 (Domain -> Entity 변환은 Repository 구현체 내부에서 수행)
            jobToRegister = jobRepository.save(jobToRegister);

        } else {
            // 메타 테이블 미사용 (Transient Object)
            log.info("Skipping Meta DB save. Constructing transient Job object.");
            jobToRegister = Job.create(
                    command.tenantId(),
                    command.jobGroup(),
                    command.jobName(),
                    command.scheduleType(),
                    command.jobType(),
                    command.cronExpression(),
                    command.command(),
                    command.parameters(),
                    command.description(),
                    command.startTime(),
                    command.endTime(),
                    command.regUserId()
            );
        }

        // 3. 스케줄러 엔진에 등록 (Quartz)
        // 주의: Quartz JobKey도 TenantId를 포함하도록 설계되었는지 확인 필요 (보통 Name에 TenantId를 prefix로 붙임)
        LocalDateTime nextFireTime = jobScheduler.register(jobToRegister);

        log.info("Job Registration Completed: [{}]{}.{}", command.tenantId(), command.jobGroup(), command.jobName());
        return nextFireTime;
    }

    /**
     * [작업 삭제]
     * 실행 중인 프로세스를 종료하고, 스케줄러와 DB에서 제거합니다.
     */
    @Transactional
    public void deleteJob(String tenantId, String jobGroup, String jobName) {
        // 1. 실행 중인 프로세스 강제 종료 (OS Level)
        // JobProcessManager도 TenantId를 인자로 받거나, 내부적으로 Unique Key를 생성해야 안전함.
        if (jobProcessManager.killJob(tenantId, jobGroup, jobName)) {
            log.info("Running process killed before deletion: [{}]{}.{}", tenantId, jobGroup, jobName);
        }

        // 2. 스케줄러에서 제거
        jobScheduler.delete(tenantId, jobGroup, jobName);

        // 3. DB 삭제
        if (properties.useMetaTable()) {
            jobRepository.delete(tenantId, jobGroup, jobName);
            log.info("Job Deleted from DB: [{}]{}.{}", tenantId, jobGroup, jobName);
        }
    }

    /**
     * [작업 강제 종료]
     */
    public boolean killJob(String tenantId, String jobGroup, String jobName) {
        boolean killed = jobProcessManager.killJob(tenantId, jobGroup, jobName);
        if (!killed) {
            // 단순히 실행 중이 아닌 것인지, 권한이 없는 것인지 구분 필요하지만 여기선 상태 체크
            throw new BusinessException(ErrorCode.JOB_NOT_RUNNING);
        }
        return true;
    }

    /**
     * [즉시 실행]
     */
    public void runJobNow(String tenantId, String jobGroup, String jobName) {
        // 1. 유효성 검증
        if (properties.useMetaTable() && !jobRepository.exists(tenantId, jobGroup, jobName)) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND);
        }

        // 2. 스케줄러에 Trigger 요청
        jobScheduler.runNow(tenantId, jobGroup, jobName);
        log.info("Requested immediate execution: [{}]{}.{}", tenantId, jobGroup, jobName);
    }

    /**
     * [작업 목록 조회 - 페이징]
     * Quartz 스케줄러의 실시간 상태를 조회합니다.
     */
    public Page<Schedule> getSchedules(String tenantId, String groupKeyword, String nameKeyword,                                 LocalDateTime from, LocalDateTime to, Pageable pageable) {
        // 1. Quartz 스케줄러에서 해당 테넌트의 모든 JobKey 또는 상세 정보를 가져오도록 호출
        // (실제 구현은 jobScheduler 내부에서 Quartz API 사용)
        log.info("tenantId : {} , groupKeyword {} , nameKeyword {}",tenantId, groupKeyword,nameKeyword);
        return jobScheduler.findSchedules(tenantId, groupKeyword, nameKeyword, from, to, pageable);
    }


}