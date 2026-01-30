package com.example.scheduler.job.domain;

import java.time.LocalDateTime;

public interface JobScheduler {
    LocalDateTime register(Job job);     // 스케줄 등록
    void delete(String jobGroup, String jobName); // 스케줄 삭제
    void runNow(String jobGroup, String jobName); // 즉시 실행
    boolean checkExists(String jobGroup, String jobName);
    boolean deleteTrigger(String triggerGroup, String triggerName);
}