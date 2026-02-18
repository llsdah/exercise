package com.example.scheduler.job.domain;

import com.example.scheduler.job.api.dto.ScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface JobScheduler {
    LocalDateTime register(Job job);     // 스케줄 등록
    void delete(String tenantId, String jobGroup, String jobName); // 스케줄 삭제
    void runNow(String tenantId, String jobGroup, String jobName); // 즉시 실행
    boolean checkExists(String tenantId, String jobGroup, String jobName);
    boolean deleteTrigger(String tenantId, String jobGroup, String jobName);
    Page<Schedule> findSchedules(String tenantId, String groupKeyword, String nameKeyword, LocalDateTime from, LocalDateTime to, Pageable pageable);
    //Schedule findSchedule(String tenantId, String jobGroup, String jobName);
}