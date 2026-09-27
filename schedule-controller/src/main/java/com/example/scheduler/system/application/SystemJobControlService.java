package com.example.scheduler.system.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;

// 수정: 감시자가 결과를 추측해 이력을 쓰지 않고 해당 실행기에 중단을 요청해 fenced 결과 기록으로 통일한다.
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemJobControlService {
    private final Scheduler scheduler;

    public int terminateHungJobs(long limitSeconds) {
        int interruptedCount = 0;
        try {
            for (var context : scheduler.getCurrentlyExecutingJobs()) {
                if (context.getJobDetail().getJobClass().getSimpleName().equals("HangCheckJob")) continue;
                long elapsed = Duration.between(context.getFireTime().toInstant(), Instant.now()).getSeconds();
                if (elapsed > limitSeconds && scheduler.interrupt(context.getFireInstanceId())) interruptedCount++;
            }
        } catch (SchedulerException failure) {
            log.warn("Watchdog interrupt failed", failure);
        }
        return interruptedCount;
    }
}
