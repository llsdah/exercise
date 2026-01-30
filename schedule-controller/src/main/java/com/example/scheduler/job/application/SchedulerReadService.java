package com.example.scheduler.job.application;

import com.example.scheduler.job.api.dto.SchedulerJobDto;
import com.example.scheduler.job.api.dto.SkipScheduleDto;
import com.example.scheduler.job.api.dto.UpcomingJobDto;
import com.example.scheduler.job.infra.persistent.JobSkipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulerReadService {

    private final Scheduler scheduler;
    private final JobSkipRepository jobSkipRepository;

    public List<SchedulerJobDto> findAllJobs() {
        List<SchedulerJobDto> jobList = new ArrayList<>();

        try {
            List<String> jobGroupNames = scheduler.getJobGroupNames();

            for (String groupName : jobGroupNames) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    JobDataMap dataMap = jobDetail.getJobDataMap();
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

                    LocalDateTime startTime = null;
                    LocalDateTime previousFireTime = null;
                    LocalDateTime nextFireTime = null;
                    String cronExpression = null;
                    String state = null;
                    if (!triggers.isEmpty()) {
                        Trigger trigger = triggers.get(0); // 첫 번째만
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        cronExpression = (trigger instanceof CronTrigger ct) ? ct.getCronExpression() : null;

                        state = triggerState.name();
                        startTime = toLocalDateTime(trigger.getStartTime());
                        previousFireTime = toLocalDateTime(trigger.getPreviousFireTime());
                        nextFireTime = toLocalDateTime(trigger.getNextFireTime());

                    }

                    // DTO 생성
                    jobList.add(SchedulerJobDto.builder()
                            .scheduleGroup(jobKey.getGroup())
                            .scheduleName(jobKey.getName())
                            .scheduleType(dataMap.containsKey("scheduleType") ? dataMap.getString("scheduleType") : "SHELL")
                            .jobType(dataMap.containsKey("jobType") ? dataMap.getString("jobType") : "CRON")
                            .command(dataMap.getString("command"))
                            .cronExpression(cronExpression)
                            .parameters(dataMap.getString("parameters"))
                            .description(jobDetail.getDescription())
                            .state(state)
                            .previousFireTime(previousFireTime)
                            .startTime(startTime)
                            .nextFireTime(nextFireTime)
                            .build());
                }
            }
        } catch (SchedulerException e) {
            log.error("Failed to retrieve scheduler jobs", e);
            throw new RuntimeException("스케줄러 조회 중 오류 발생", e);
        }

        return jobList;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    /**
     * [검색] 실행 예정 작업 조회
     * 조건: Group(필수), Name(선택), Start~End(선택)
     */
    public List<UpcomingJobDto> searchUpcomingJobs(
            String group,
            String jobName,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        List<UpcomingJobDto> resultList = new ArrayList<>();

        // 시간 비교를 위한 Date 변환 (null이면 조건 체크 안 함)
        Date searchStart = (startAt != null) ? Date.from(startAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
        Date searchEnd = (endAt != null) ? Date.from(endAt.atZone(ZoneId.systemDefault()).toInstant()) : null;

        try {
            // 1. [최적화] 전체 스캔 대신, GroupMatcher로 해당 그룹의 트리거만 가져옴 (1차 필터)
            // (Group은 필수이므로 무조건 범위를 좁힐 수 있음)
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group));

            for (TriggerKey triggerKey : triggerKeys) {
                Trigger trigger = scheduler.getTrigger(triggerKey);

                // 작업 정보 조회 (JobKey를 알아야 함)
                JobKey jobKey = trigger.getJobKey();

                // 2. [필터] Job Name 조건 체크 (입력된 경우에만)
                if (jobName != null && !jobName.isBlank()) {
                    if (!jobKey.getName().equals(jobName)) {
                        continue; // 이름이 다르면 스킵
                    }
                }

                Date nextFireTime = trigger.getNextFireTime();

                // 3. [필터] 실행 예정 시간이 없는 경우(종료됨) 스킵
                if (nextFireTime == null) {
                    continue;
                }

                // 4. [필터] StartTime 조건 체크 (입력된 경우에만)
                // "검색 시작 시간보다 이르면(과거이면) 제외" -> 즉, startAt 이후여야 함
                if (searchStart != null && nextFireTime.before(searchStart)) {
                    continue;
                }

                // 5. [필터] EndTime 조건 체크 (입력된 경우에만)
                // "검색 종료 시간보다 늦으면(미래이면) 제외" -> 즉, endAt 이전이어야 함
                if (searchEnd != null && nextFireTime.after(searchEnd)) {
                    continue;
                }

                // 6. 상세 정보 매핑 (JobDetail 조회)
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                JobDataMap dataMap = jobDetail.getJobDataMap();
                String cronExpression = (trigger instanceof CronTrigger ct) ? ct.getCronExpression() : null;
                resultList.add(UpcomingJobDto.builder()
                        .scheduleGroup(jobKey.getGroup())
                        .scheduleName(jobKey.getName())

                        // [추가] 조회된 Trigger의 실제 Key 매핑
                        .triggerGroup(trigger.getKey().getGroup())
                        .triggerName(trigger.getKey().getName())

                        .type(trigger instanceof CronTrigger ? "CRON" : "ONCE")
                        .command(dataMap.getString("command"))
                        .parameters(dataMap.getString("parameters"))
                        .cronExpression(cronExpression)
                        .nextFireTime(toLocalDateTime(nextFireTime))
                        .prevFireTime(toLocalDateTime(trigger.getPreviousFireTime()))
                        .build());
            }

            // 7. 결과 정렬 (시간순)
            resultList.sort(Comparator.comparing(UpcomingJobDto::nextFireTime));

            return resultList;

        } catch (SchedulerException e) {
            log.error("Failed to search jobs. Group: {}, Name: {}", group, jobName, e);
            throw new RuntimeException("스케줄 검색 중 오류 발생", e);
        }
    }

    /**
     * 실행 건너뛰기(Skip) 예약 목록 조회
     * - job_skip_log 테이블에 있는 데이터는 '미래의 스킵 예정' 데이터입니다.
     */
    @Transactional(readOnly = true)
    public List<SkipScheduleDto> getPendingSkipSchedules() {
        // skipTime 기준 오름차순 정렬 (가장 가까운 미래부터 표시)
        //Sort.by(Sort.Direction.ASC, "skipTime")
        return jobSkipRepository.findAll()
                .stream()
                .map(SkipScheduleDto::from)
                .collect(Collectors.toList());
    }

}