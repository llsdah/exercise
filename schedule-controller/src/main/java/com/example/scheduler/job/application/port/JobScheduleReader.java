package com.example.scheduler.job.application.port;

import com.example.scheduler.job.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface JobScheduleReader {    // 조회
    Page<Schedule> findSchedules(String tenantId, String groupKeyword, String nameKeyword, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Schedule> findNextFireTimes(String tenantId, String scheduleGroup, String scheduleName, Pageable pageable);
}