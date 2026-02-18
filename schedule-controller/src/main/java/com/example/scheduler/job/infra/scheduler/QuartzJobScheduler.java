package com.example.scheduler.job.infra.scheduler;

import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.error.BusinessException;
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.domain.JobScheduler;
import com.example.scheduler.job.domain.JobStatus;
import com.example.scheduler.job.domain.Schedule;
import com.example.scheduler.job.infra.executor.ShellCommandJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJobScheduler implements JobScheduler {

    private final Scheduler scheduler;

    @Override
    public LocalDateTime register(Job job) {
        log.info("Job info : {}", job);

        Date nextFireTime = null;
        String quartzGroup = ScheduleKeyPolicy.jobGroup(job.getTenantId(), job.getJobGroup());
        String triggerName = ScheduleKeyPolicy.triggerName(job.getJobName());

        try {
            // 키 생성
            JobKey jobKey = JobKey.jobKey(job.getJobName(), quartzGroup);
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, quartzGroup);

            // 1. JobDataMap에 필요한 정보 담기
            JobDetail jobDetail = JobBuilder.newJob(ShellCommandJob.class)
                    .withIdentity(jobKey)
                    .withDescription(job.getDescription())
                    .usingJobData("cronExpression", job.getCronExpression())
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
                if (job.getScheduleStartTime() != null) {
                    triggerBuilder.startAt(toDate(job.getScheduleStartTime()));
                } else {
                    triggerBuilder.startNow(); // 없으면 즉시 시작
                }

                // 종료 시간 설정
                if (job.getScheduleEndTime() != null) {
                    triggerBuilder.endAt(toDate(job.getScheduleEndTime()));
                }

                CronTrigger trigger = triggerBuilder.build();

                // 4. 기존 트리거가 있으면 갱신(Reschedule), 없으면 신규 등록
                if (scheduler.checkExists(triggerKey)) {
                    nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
                } else {
                    nextFireTime = scheduler.scheduleJob(trigger);
                }
                log.info("Trigger Registered: {} (Start: {}, End: {})", triggerKey, job.getScheduleStartTime(), job.getScheduleEndTime());

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
    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public void delete(String tenantId, String jobGroup, String jobName) {

        String quartzGroup = ScheduleKeyPolicy.jobGroup(tenantId, jobGroup);
        String triggerName = ScheduleKeyPolicy.triggerName(jobName);

        try {
            boolean result = scheduler.deleteJob(JobKey.jobKey(jobName, quartzGroup));

            if (result) {
                log.info("deleteJob success");
            } else {
                throw new SchedulerException("deleteJob fail");
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Quartz delete failed", e);
        }
    }

    @Override
    public void runNow(String tenantId, String jobGroup, String jobName) {

        String quartzGroup = ScheduleKeyPolicy.jobGroup(tenantId, jobGroup);

        try {
            JobKey jobKey = JobKey.jobKey(jobName, quartzGroup);

            // 존재 여부 확인
            if (!scheduler.checkExists(jobKey)) {
                throw new IllegalArgumentException("Job not found Group" + quartzGroup + ", Name : " + jobName);
            }

            scheduler.triggerJob(jobKey);

        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to trigger job Group : " + quartzGroup + ", Name : " + jobName, e);
        }
    }

    @Override
    public boolean checkExists(String tenantId, String jobGroup, String jobName) {

        String quartzGroup = ScheduleKeyPolicy.jobGroup(tenantId, jobGroup);
        try {
            // Quartz의 JobKey 생성
            JobKey jobKey = JobKey.jobKey(jobName, quartzGroup);

            // 존재 여부 확인 (true/false 반환)
            return scheduler.checkExists(jobKey);

        } catch (SchedulerException e) {
            log.error("Failed to check job existence in Quartz", e);
            // 체크 중 에러가 나면 시스템 오류로 간주하고 런타임 예외 발생
            throw new RuntimeException("Quartz Job 존재 여부 확인 실패", e);
        }
    }

    @Override
    public boolean deleteTrigger(String tenantId, String jobGroup, String jobName) {

        String quartzGroup = ScheduleKeyPolicy.jobGroup(tenantId, jobGroup);
        String triggerName = ScheduleKeyPolicy.triggerName(jobName);

        try {
            log.warn("To delete trigger group {}, name {}", quartzGroup, triggerName);
            TriggerKey key = TriggerKey.triggerKey(triggerName, quartzGroup);

            // unscheduleJob: 트리거만 삭제하고, 연결된 Job은 남겨둠
            return scheduler.unscheduleJob(key);

        } catch (SchedulerException e) {
            log.error("Failed to delete trigger", e);
            throw new RuntimeException("트리거 삭제 실패", e);
        }
    }


    /**
     * [모니터링 검색] Quartz 실시간 조회 + 페이징 + 필터링
     */
    public Page<Schedule> findSchedules(String tenantId, String groupKeyword, String nameKeyword,
                                          LocalDateTime from, LocalDateTime to,
                                          Pageable pageable) {
        List<Schedule> fullList = new ArrayList<>();
        log.info("작업 조회를 위한 내용 : ");

        try {
            // 1. [검색 범위 결정]
            GroupMatcher<JobKey> matcher;
            if (StringUtils.hasText(groupKeyword)) {
                // 특정 그룹: "tenant group" 정확 일치
                String fullGroup = ScheduleKeyPolicy.jobGroup(tenantId, groupKeyword);
                matcher = GroupMatcher.jobGroupEquals(fullGroup);
            } else {
                // 전체 그룹: "tenant" 로 시작하는 모든 그룹
                matcher = GroupMatcher.jobGroupStartsWith(tenantId);
            }

            // 2. [Key 조회] Quartz 메모리/DB에서 Key Set 가져오기
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);

            // 3. [필터링 & DTO 변환]
            for (JobKey jobKey : jobKeys) {
                log.info("작업 조회를 위한 내용 : {}",jobKey.getName());
                // 3-1. 이름 검색 (Keyword가 있을 때만 체크)
                if (StringUtils.hasText(nameKeyword) && !jobKey.getName().contains(nameKeyword)) {
                    continue;
                }

                // 3-2. Trigger 조회
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

                // [중요] 시간 검색 조건이 있는데 Trigger가 없으면 -> 조회 대상에서 제외
                boolean hasTimeCondition = (from != null || to != null);
                if (triggers.isEmpty() && hasTimeCondition) {
                    continue;
                }

                // Primary Trigger 선정 (없으면 null)
                Trigger primaryTrigger = triggers.isEmpty() ? null : triggers.get(0);
                LocalDateTime nextFireTime = (primaryTrigger != null)
                        ? toLocalDateTime(primaryTrigger.getNextFireTime()) : null;

                // 3-3. 시간 검색 (Trigger가 있고, 범위 밖이면 제외)
                if (from != null && (nextFireTime == null || nextFireTime.isBefore(from))) continue;
                if (to != null && (nextFireTime == null || nextFireTime.isAfter(to))) continue;

                // 3-4. 상세 정보 조회
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                Trigger.TriggerState triggerState = (primaryTrigger != null)
                        ? scheduler.getTriggerState(primaryTrigger.getKey())
                        : Trigger.TriggerState.NONE;

                // 3-5. 리스트 추가
                fullList.add(toSchedule(tenantId, jobKey, jobDetail, primaryTrigger, triggerState));
            }

        } catch (SchedulerException e) {
            log.error("Failed to search jobs from Quartz.", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 4. [정렬] 다음 실행 시간이 빠른 순서대로 정렬 (null은 마지막)
        fullList.sort(Comparator.comparing(Schedule::getNextFireTime, Comparator.nullsLast(Comparator.naturalOrder())));

        // 5. [페이징] 리스트 자르기
        return paginate(fullList, pageable);
    }

    private Schedule toSchedule(String tenantId, JobKey jobKey, JobDetail jobDetail,
                                Trigger trigger, Trigger.TriggerState state) {

        String logicGroup = ScheduleKeyPolicy.extractGroup(jobKey.getGroup());

        // Trigger가 null일 수 있음을 고려하여 안전하게 접근
        String cronExp = (trigger instanceof CronTrigger ct) ? ct.getCronExpression() : null;
        LocalDateTime nextTime = (trigger != null) ? toLocalDateTime(trigger.getNextFireTime()) : null;
        LocalDateTime prevTime = (trigger != null) ? toLocalDateTime(trigger.getPreviousFireTime()) : null;

        // JobDataMap에서 command, parameters 추출
        JobDataMap dataMap = jobDetail.getJobDataMap();

        return Schedule.builder()
                .tenantId(tenantId)
                .jobGroup(logicGroup)
                .jobName(jobKey.getName())
                .jobStatus(JobStatus.fromQuartzState(state.name()))
                .cronExpression(cronExp)
                .nextFireTime(nextTime)
                .prevFireTime(prevTime)
                .description(jobDetail.getDescription())
                .jobClass(jobDetail.getJobClass().getSimpleName())
                .command(dataMap.getString("command"))      // [중요]
                .parameters(dataMap.getString("parameters"))// [중요]
                .build();
    }

    private Page<Schedule> paginate(List<Schedule> list, Pageable pageable) {
        int total = list.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        if (start > total) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        List<Schedule> content = list.subList(start, end);
        return new PageImpl<>(content, pageable, total);
    }


}