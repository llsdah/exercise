package com.example.scheduler.job.application;

import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.global.error.BusinessException;
import com.example.scheduler.job.api.dto.JobSkipSearchRequest;
import com.example.scheduler.job.application.model.JobSkipCommand;
import com.example.scheduler.job.domain.JobSkipSearchCondition;
import com.example.scheduler.job.domain.JobSkip;
import com.example.scheduler.job.domain.JobSkipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobSkipService {

    private final JobSkipRepository jobSkipRepository;
    private final SchedulerProperties properties;

    @Transactional
    public JobSkip addSkipSchedule(JobSkipCommand jobSkipCommand) {
        JobSkip result = null;
        String tenantId = jobSkipCommand.tenantId();
        String group = jobSkipCommand.jobGroup();
        String name = jobSkipCommand.jobName();
        LocalDateTime targetTime = jobSkipCommand.fireTime().truncatedTo(ChronoUnit.MINUTES);

        if (jobSkipRepository.findJobSkip(tenantId, group, name, targetTime).isPresent()) {
            // 이미 등록된 시간이면 예외 처리하거나, 성공으로 간주하고 기존 값 리턴
            log.warn("Skip schedule already exists: {}", targetTime);
            return result;
        }

        long currentCount = jobSkipRepository.countByJob(tenantId, group, name);

        if (currentCount >= properties.maxSkipCount()) {
            log.error("Skip limit exceeded for [{}]{}.{}. Current: {}, Max: {}",
                    tenantId, group, name, currentCount, properties.maxSkipCount());
            throw new BusinessException(ErrorCode.SKIP_SLOT_FULL);
        }

        // Service에서는 간단히 호출
        JobSkip newSkip = JobSkip.create(tenantId, group, name, targetTime, jobSkipCommand.regUserId());

        // 3. 저장
        result = jobSkipRepository.save(newSkip);

        log.info("Registered Skip: [{}]{}.{} at {}", tenantId, group, name, targetTime);

        return result;
    }

    @Transactional
    public void cancelSkipSchedule(String tenantId, String jobGroup, String jobName, LocalDateTime skipTime) {
        LocalDateTime targetTime = skipTime.truncatedTo(ChronoUnit.MINUTES);

        // 1. 존재 확인 (선택 사항)
        // 없을 때 에러를 낼지, 그냥 무시할지에 따라 로직 결정
        // JPA deleteById는 없으면 에러(`EmptyResultDataAccessException`)를 낼 수 있으므로 체크 추천
        boolean exists = jobSkipRepository.findJobSkip(tenantId, jobGroup, jobName, targetTime).isPresent();

        if (!exists) {
            throw new BusinessException(ErrorCode.SKIP_NOT_FOUND);
        }

        // 2. 삭제 (PK로 즉시 삭제)
        jobSkipRepository.delete(tenantId, jobGroup, jobName, targetTime);

        log.info("Canceled Skip: [{}]{}.{} at {}", tenantId, jobGroup, jobName, targetTime);
    }

    /**
     * 실행 건너뛰기(Skip) 예약 목록 조회, '미래의 스킵 예정' 데이터입니다.
     */
    public Page<JobSkip> getPendingSkipSchedules(JobSkipSearchRequest searchRequest, Pageable pageable) {
        JobSkipSearchCondition condition = new JobSkipSearchCondition(
                searchRequest.tenantId(),
                searchRequest.scheduleGroup(),
                searchRequest.scheduleName(),
                searchRequest.from(),
                searchRequest.to()
        );

        // 2. Repository에는 내부 전용 객체와 페이징 정보만 전달
        return jobSkipRepository.findByConditions(condition, pageable);
    }



    /**
     // =========================================================================
     // Skip Logic (추후 JobSkipEntity도 Multi-tenant 적용 필요)
     // =========================================================================
     @Transactional
     public ScheduleSkipResponse addSkipSchedule(JobSkipCommand skipJobCommand) {
     String tenantId = skipJobCommand.tenantId();
     String scheduleGroup = skipJobCommand.scheduleGroup();
     String scheduleName = skipJobCommand.scheduleName();
     LocalDateTime targetTime = skipJobCommand.fireTime().truncatedTo(ChronoUnit.MINUTES);
     LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

     // 1. 도메인 모델 조회 (없으면 도메인 객체 새로 생성)
     // [수정] Entity가 아니라 Domain Model(JobSkip)을 다룹니다.
     JobSkip jobSkip = jobSkipRepository.findJobSkip(tenantId, scheduleGroup, scheduleName)
     .orElseGet(() -> JobSkip.builder()
     .tenantId(tenantId)        // [해결] TenantID 포함
     .scheduleGroup(scheduleGroup)
     .scheduleName(scheduleName)
     .build());

     // 2. 비즈니스 로직 수행 (도메인 메서드 호출)
     boolean success = jobSkip.addSkipTime(targetTime, now);

     if (!success) {
     throw new BusinessException(ErrorCode.SKIP_SLOT_FULL);
     }

     // 3. 저장 (Infrastructure 계층이 알아서 Entity로 변환하여 저장함)
     jobSkipRepository.save(jobSkip);

     log.info("Registered Skip: [{}]{}.{} at {}", tenantId, scheduleGroup, scheduleName, targetTime);

     // 4. DTO 반환 (Domain -> DTO 변환)
     return ScheduleSkipResponse.from(jobSkip);
     }

     @Transactional
     public void cancelSkipSchedule(String tenantId, String scheduleGroup, String scheduleName, LocalDateTime skipTime) {
     LocalDateTime targetTime = skipTime.truncatedTo(ChronoUnit.MINUTES);

     // 1. 도메인 모델 조회
     JobSkip jobSkip = jobSkipRepository.findJobSkip(tenantId, scheduleGroup, scheduleName)
     .orElseThrow(() -> new BusinessException(ErrorCode.SKIP_NOT_FOUND));

     // 2. 비즈니스 로직 수행
     boolean removed = jobSkip.consumeTime(targetTime); // 혹은 removeSkipTime 사용
     // (앞서 작성한 도메인 모델에 removeSkipTime 로직이 있다면 그것을 사용)

     if (!removed) {
     throw new BusinessException(ErrorCode.SKIP_NOT_FOUND);
     }

     // 3. 변경 상태 저장
     jobSkipRepository.save(jobSkip);

     log.info("Canceled Skip: [{}]{}.{} at {}", tenantId, scheduleGroup, scheduleName, targetTime);
     }

     */
}
