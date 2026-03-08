package com.example.scheduler.job.application.port;

import com.example.scheduler.job.domain.Job;
import org.quartz.JobKey;
import java.time.LocalDateTime;

public interface JobScheduleCommand {
    LocalDateTime register(Job job);     // 스케줄 등록
    void delete(String tenantId, String jobGroup, String jobName); // 스케줄 삭제
    void runNow(String tenantId, String jobGroup, String jobName); // 즉시 실행
    boolean checkExists(String tenantId, String jobGroup, String jobName);
    boolean deleteTrigger(String tenantId, String jobGroup, String jobName);
    void pauseJob(JobKey jobKey);    // ← 추가
    void resumeJob(JobKey jobKey);  // ← 추가
}