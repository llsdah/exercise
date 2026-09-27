package com.example.scheduler.job.infra.watchdog;

import com.example.scheduler.job.infra.executor.JobProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ============================================================
 * WatchdogScheduler
 * ============================================================
 *
 * 목적:
 *  - Quartz 자체가 감지하지 못하는 "실행 실패 상태"를 감시한다.
 *  - 오래 실행되거나 멈춘(hang) Job을 탐지하고 강제 종료한다.
 *
 * 설계 의도:
 *  - Quartz는 "언제 실행할지"만 책임진다.
 *  - Job이 정상 종료되는지는 Quartz의 책임이 아니다.
 *  - 따라서 실행 상태 관측 + 강제 종료는 별도의 Watchdog이 담당한다.
 *
 * 이 클래스의 역할:
 *  1) Quartz ThreadPool 상태 관측 (관측만, 제어 X)
 *  2) Executor(Local) 기준 실행 시간 초과 Job 감지
     *  3) Attempt별 Process 종료 요청
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class Watchdog {

    /**
     * 현재 JVM에서 실행 중인 Job 프로세스 정보를 관리하는 컴포넌트
     * - 시작 시각
     * - timeout
     * - 실제 kill 로직 보유
     */
    private final JobProcessManager jobProcessManager;

    /**
     * Quartz Scheduler
     * - ThreadPool 상태 조회
     * - 현재 실행 중인 Job 목록 조회
     * - interrupt 요청 (best-effort)
     */
    private final Scheduler scheduler;

    /**
     * Watchdog 메인 루프
     *
     * fixedDelay:
     *  - 이전 실행이 끝난 시점 기준으로 delay
     *  - Watchdog 중복 실행 방지에 유리
     */
    @Scheduled(fixedDelay = 60_000) // 60초마다 실행
    public void monitorAndCleanup() {
        log.info("🔍 [Watchdog] Starting system health check...");

        try {
            // 1️⃣ Quartz 실행 환경 상태 관측 (절대 kill 하지 않음)
            checkSchedulerHealth();

            // 2️⃣ 실제 실행 중인 Job을 기준으로 timeout 초과 여부 판단
            cleanupZombieJobs();

        } catch (Exception e) {
            // Watchdog 자체가 죽으면 더 큰 사고이므로 예외를 삼킨다
            log.error("❌ [Watchdog] Error during health check", e);
        }
    }

    /**
     * Quartz Scheduler 상태 관측 메서드
     *
     * 관측 대상:
     *  - ThreadPool 전체 크기
     *  - 현재 실행 중인 Job 수
     *
     * 주의:
     *  - 여기서는 "경보"만 한다.
     *  - Quartz ThreadPool이 꽉 찼다고 해서
     *    반드시 장애는 아니다 (I/O 대기일 수 있음).
     */
    private void checkSchedulerHealth() throws SchedulerException {

        // Quartz 메타데이터 (읽기 전용)
        SchedulerMetaData metaData = scheduler.getMetaData();

        // Quartz worker thread 총 개수
        int threadPoolSize = metaData.getThreadPoolSize();

        // 현재 Quartz 기준 실행 중인 Job 목록
        List<JobExecutionContext> currentlyExecutingJobs =
                scheduler.getCurrentlyExecutingJobs();

        int activeCount = currentlyExecutingJobs.size();

        log.info(
                "📊 [Quartz Stat] Thread Pool Usage: {}/{} (Active/Total)",
                activeCount,
                threadPoolSize
        );

        // ThreadPool 포화 상태 경고
        if (activeCount >= threadPoolSize) {
            log.warn(
                    "⚠️ [Alert] Quartz thread pool is EXHAUSTED. " +
                            "No free worker threads available."
            );
        }
    }

    /**
     * 실제 실행 중인 Job을 기준으로 한 Zombie Job 정리 로직
     *
     * 기준:
     *  - JobProcessManager에 등록된 scheduleStartTime + timeout
     *
     * 수정: Attempt 단위로 종료를 요청하고 실행기가 fenced 결과를 기록한다.
     */
    private void cleanupZombieJobs() {
        long now = System.currentTimeMillis();

        jobProcessManager.getRunningProcesses().forEach((processKey, info) -> {
            long durationSeconds = (now - info.getStartTime()) / 1000;

            if (durationSeconds > info.getTimeoutSeconds()) {
                log.warn("🚨 [Watchdog] Job [{}] exceeded timeout. duration={}s, timeout={}s.",
                        processKey.jobName(), durationSeconds, info.getTimeoutSeconds());

                // 수정: timeout에 해당하는 Attempt만 종료하고 실행기가 fenced 결과를 기록한다.
                jobProcessManager.killProcess(processKey);
            }
        });
    }
}
