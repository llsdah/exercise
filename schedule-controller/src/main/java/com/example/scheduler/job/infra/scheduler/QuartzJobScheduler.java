package com.example.scheduler.job.infra.scheduler;

import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobScheduler;
import com.example.scheduler.job.infra.executor.ShellCommandJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJobScheduler implements JobScheduler {

    private final Scheduler scheduler;

    @Override
    public LocalDateTime register(Job job) {
        log.info("Job info : {}", job); // toString() 자동 호출됨

        Date nextFireTime = null;
        try {
            // 키 생성
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName() + "_trigger", job.getJobGroup());

            // 1. JobDataMap에 필요한 정보 담기 (사용자님 코드 반영)
            JobDetail jobDetail = JobBuilder.newJob(ShellCommandJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("jobId", job.getId()) // DB ID (없으면 null일 수 있음 주의)
                    .usingJobData("command", job.getCommand())
                    .usingJobData("parameters", job.getParameters())
                    .usingJobData("jobType", job.getJobType())         // SHELL, HTTP 등
                    .usingJobData("scheduleType", job.getScheduleType()) // CRON, ONCE 등
                    .storeDurably()
                    .build();

            // 2. Job 등록 (replace = true: 기존 정보 덮어쓰기)
            scheduler.addJob(jobDetail, true);

            // 3. Cron 표현식이 유효한 경우에만 트리거 생성 및 연결
            // (jobType 체크보다는 실제 cron 식 존재 여부가 더 확실함)
            if (job.getCronExpression() != null && !job.getCronExpression().isBlank()) {

                // [통합] Trigger 생성 (StartAt / EndAt 적용)
                TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .forJob(jobDetail)
                        .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()));

                // 시작 시간 설정
                if (job.getStartTime() != null) {
                    triggerBuilder.startAt(toDate(job.getStartTime()));
                } else {
                    triggerBuilder.startNow(); // 없으면 즉시 시작
                }

                // 종료 시간 설정
                if (job.getEndTime() != null) {
                    triggerBuilder.endAt(toDate(job.getEndTime()));
                }

                CronTrigger trigger = triggerBuilder.build();

                // 4. 기존 트리거가 있으면 갱신(Reschedule), 없으면 신규 등록
                if (scheduler.checkExists(triggerKey)) {
                    nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
                } else {
                    nextFireTime = scheduler.scheduleJob(trigger);
                }
                log.info("Trigger Registered: {} (Start: {}, End: {})", triggerKey, job.getStartTime(), job.getEndTime());

            } else {
                // 5. Cron이 없거나 지워진 경우 -> 기존 트리거 삭제 (수동 모드 전환)
                if (scheduler.checkExists(triggerKey)) {
                    boolean unscheduled = scheduler.unscheduleJob(triggerKey);
                    if (unscheduled) {
                        log.info("Existing trigger removed (Switched to Manual Mode): {}", triggerKey);
                    }
                }
            }

        } catch (SchedulerException e) {
            log.error("Quartz scheduling failed for Job: {}", job.getJobName(), e);
            throw new RuntimeException("Quartz scheduling failed", e);
        }

        return nextFireTime == null ? null : this.toLocalDateTime(nextFireTime);
    }

    // LocalDateTime -> Date 변환 유틸
    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
    // Date -> LocalDateTime 변환 유틸
    private LocalDateTime toLocalDateTime(Date date) { return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();}

    @Override
    public void delete(String jobGroup, String jobName) {
        try {
            boolean result = scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));

            if (result) {
                log.info("deleteJob success");
            } else {
                log.info("deleteJob fail");
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Quartz delete failed", e);
        }
    }

    @Override
    public void runNow(String jobGroup, String jobName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

            // 존재 여부 확인
            if (!scheduler.checkExists(jobKey)) {
                throw new IllegalArgumentException("Job not found Group" + jobGroup + ", Name : "+ jobName);
            }

            scheduler.triggerJob(jobKey);

        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to trigger job Group : " + jobGroup + ", Name : " + jobName, e);
        }
    }


    @Override
    public boolean checkExists(String jobGroup, String jobName) {
        try {
            // Quartz의 JobKey 생성
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

            // 존재 여부 확인 (true/false 반환)
            return scheduler.checkExists(jobKey);

        } catch (SchedulerException e) {
            log.error("Failed to check job existence in Quartz", e);
            // 체크 중 에러가 나면 시스템 오류로 간주하고 런타임 예외 발생
            throw new RuntimeException("Quartz Job 존재 여부 확인 실패", e);
        }
    }

    // 인터페이스에도 추가 필요: boolean deleteTrigger(String group, String name);

    @Override
    public boolean deleteTrigger(String triggerGroup, String triggerName) {
        try {
            TriggerKey key = TriggerKey.triggerKey(triggerName, triggerGroup);

            // unscheduleJob: 트리거만 삭제하고, 연결된 Job은 남겨둠
            return scheduler.unscheduleJob(key);

        } catch (SchedulerException e) {
            log.error("Failed to delete trigger", e);
            throw new RuntimeException("트리거 삭제 실패", e);
        }
    }

}