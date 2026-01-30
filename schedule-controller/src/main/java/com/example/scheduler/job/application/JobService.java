package com.example.scheduler.job.application;

import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.error.BusinessException;
import com.example.scheduler.job.api.dto.SkipScheduleDto;
import com.example.scheduler.job.application.model.JobCommand;
import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobRepository;
import com.example.scheduler.job.domain.JobScheduler;
import com.example.scheduler.job.infra.executor.JobProcessManager; // 예외적으로 Process 제어는 Infra 참조 허용 또는 인터페이스화
import com.example.scheduler.job.infra.persistent.JobSkipEntity;
import com.example.scheduler.job.infra.persistent.JobSkipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobSkipRepository jobSkipRepository;
    private final JobScheduler jobScheduler;
    private final JobProcessManager jobProcessManager; // 실행 중인 프로세스 제어

    // 설정값 주입 (기본값 true)
    @Value("${app.scheduler.use-meta-table:true}")
    private boolean useMetaTable;

    @Transactional
    public LocalDateTime registerOrUpdateJob(JobCommand command) {
        log.debug("Job domain {} ", command);

        Job jobToRegister;
        if (useMetaTable) {

            // 1. 기존 작업이 있는지 조회
            jobToRegister = jobRepository.findByScheduleGroupAndScheduleName(command.jobGroup(), command.jobName())
                    .map(existingJob -> {
                        // 1-A. 있으면: 기존 객체의 내용 수정 (Update)
                        log.info("Updating existing job: {}", command.jobName());
                        existingJob.update(command.cronExpression(), command.command(), command.parameters(), command.jobUser());
                        return existingJob;
                    })
                    .orElseGet(() -> {
                        // 1-B. 없으면: 새로운 객체 생성 (Create)
                        log.info("Creating new job Group {}, Name {} ", command.jobGroup(), command.jobName());
                        return Job.create(command.jobGroup(), command.jobName(), command.scheduleType() , command.jobType(), command.cronExpression(), command.command(), command.parameters(), command.startTime(), command.endTime(), command.jobUser());
                    });


            // 2. DB 저장 (JPA 구현체에서 ID가 있으면 update, 없으면 insert 쿼리 발생)
            jobToRegister = jobRepository.save(jobToRegister);
        } else {
            // 메타 테이블 미사용 (Quartz 직접 연결)
            log.info("Skipping Meta DB save. constructing transient Job object.");

            // DB ID 없이 메모리 상에서만 Job 도메인 객체 생성
            jobToRegister = Job.create(
                    command.jobGroup(),
                    command.jobName(),
                    command.scheduleType(),
                    command.jobType(),
                    command.cronExpression(),
                    command.command(),
                    command.parameters(),
                    command.startTime(),
                    command.endTime(),
                    command.jobUser()
            );
        }

        // 3. 스케줄러 등록 (Quartz)
        // (기존 QuartzJobScheduler 구현에 '이미 있으면 삭제 후 재등록' 로직이 있으므로 그대로 호출하면 됨)
        LocalDateTime nextFireTime = jobScheduler.register(jobToRegister);

        log.info("Job Registration Completed Group {}, Name {} ",command.jobGroup(), command.jobName());
        return nextFireTime;
    }

    @Transactional
    public void deleteJob(String jobGroup, String jobName) {
        // 1. 실행 중인 프로세스 강제 종료 (Kill)
        if (jobProcessManager.killJob(jobGroup, jobName)) {
            log.info("Running process killed before deletion: Group {}, name {}",jobGroup, jobName);
        }

        // 2. 스케줄러에서 제거 (Quartz)
        jobScheduler.delete(jobGroup, jobName);

        // 3. DB 삭제
        if (useMetaTable) {
            jobRepository.deleteByScheduleGroupAndScheduleName(jobGroup, jobName);
            log.info("Job Deleted: {}", jobName);
        } else {
            log.info("Skipping Meta DB deletion ");
        }
    }
    
    public boolean killJob(String jobGroup, String jobName) {
        boolean killed = jobProcessManager.killJob(jobGroup, jobName);
        if (!killed) {
            throw new IllegalStateException("Job is not running: " + jobGroup + " " + jobName);
        }
        return killed;
    }

    public void runJobNow(String jobGroup, String jobName) {
        // 1. DB에 존재하는지 확인
        if ( useMetaTable && !jobRepository.existsByScheduleGroupAndScheduleName(jobGroup, jobName)) {
            throw new IllegalArgumentException("Job does not exist in DB: " + jobGroup + " " + jobName);
        } else {
            // Scheduler 인터페이스에 checkExists 메서드가 있다면 호출 추천
            if (!jobScheduler.checkExists(jobGroup, jobName)) {
                throw new IllegalArgumentException("Job does not exist in scheduler : " + jobGroup + " " + jobName);
            }
        }

        // 2. 스케줄러에 실행 요청
        jobScheduler.runNow(jobGroup, jobName);
        log.info("Requested immediate execution for Job: Group {}, Name {} ",jobGroup, jobName);
    }


    @Transactional
    public SkipScheduleDto addSkipSchedule(String jobGroup, String jobName, LocalDateTime newSkipTime) {
        // 초 단위 절삭
        LocalDateTime targetTime = newSkipTime.truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 1. 조회 or 생성
        JobSkipEntity entity = jobSkipRepository.findByJobGroupIgnoreCaseAndJobNameIgnoreCase(jobGroup, jobName)
                .orElseGet(() -> JobSkipEntity.builder()
                        .jobGroup(jobGroup)
                        .jobName(jobName)
                        .build());

        // 2. 슬롯에 추가 시도
        boolean success = entity.addSkipTime(targetTime, now);

        if (!success) {
            // "스킵 슬롯이 꽉 찼습니다" 같은 텍스트 없음 -> Enum만 사용
            throw new BusinessException(ErrorCode.SKIP_SLOT_FULL);
        }

        // 3. 저장
        jobSkipRepository.save(entity);
        log.info("Registered Skip: [{}]{} at {}", jobGroup, jobName, targetTime);

        return SkipScheduleDto.from(entity);
    }

    /**
     * [스킵 취소]
     * 특정 시간의 스킵 예약을 삭제합니다.
     */
    @Transactional
    public void cancelSkipSchedule(String jobGroup, String jobName, LocalDateTime skipTime) {
        // 시간 비교를 위해 초 단위 절삭 (등록할 때와 동일한 기준)
        LocalDateTime targetTime = skipTime.truncatedTo(ChronoUnit.SECONDS);

        // 1. 조회 (대소문자 무시)
        JobSkipEntity entity = jobSkipRepository
                .findByJobGroupIgnoreCaseAndJobNameIgnoreCase(jobGroup, jobName)
                .orElseThrow(() -> new IllegalArgumentException("해당 작업의 스킵 예약 정보가 없습니다."));

        // 2. 삭제 시도
        boolean removed = entity.removeSkipTime(targetTime);

        if (!removed) {
            throw new BusinessException(ErrorCode.SKIP_NOT_FOUND);
        }

        // 3. 변경사항 저장
        // (모든 슬롯이 다 null이 되어도, Row는 남겨두는 게 재사용 측면에서 유리합니다)
        jobSkipRepository.save(entity);

        log.info("Canceled Skip: [{}]{} at {}", jobGroup, jobName, targetTime);
    }

}