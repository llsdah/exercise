package com.example.scheduler.lease.infra.watchdog;

import com.example.scheduler.execution.application.ExecutionCoordinator;
import com.example.scheduler.global.logging.ExecutionLogContext;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.global.config.ExecutionProperties;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;
import java.util.*;

// 수정: 스케줄링/스레드 관리는 infra에 두고 실행 서비스로 Lease 갱신과 만료 처리를 요청한다.
@Component
@EnableScheduling
@EnableConfigurationProperties(ExecutionProperties.class)
@Slf4j
public class ExecutionLeaseMonitor {
    private final ExecutionCoordinator coordinator;
    private final ExecutionProperties properties;
    private final long guardTtlNanos;
    private final ScheduledExecutorService heartbeats = Executors.newScheduledThreadPool(2,
            Thread.ofPlatform().daemon().name("execution-lease-", 0).factory());

    public ExecutionLeaseMonitor(ExecutionCoordinator coordinator, ExecutionProperties properties) {
        this.coordinator = coordinator;
        this.properties = properties;
        guardTtlNanos = properties.leaseTtl().toNanos();
    }

    public Guard watch(LeaseClaim claim) { return new Guard(claim); }

    @Scheduled(fixedDelayString = "${app.execution.sweep-interval-ms:5000}")
    public void sweep() {
        try {
            int expired = coordinator.expireLeases();
            if (expired > 0) log.warn("{} executions moved to UNKNOWN after lease expiration", expired);
        } catch (RuntimeException failure) {
            log.warn("Lease scan unavailable; no new process is started by recovery", failure);
        }
    }

    @PreDestroy
    public void shutdown() { heartbeats.shutdownNow(); }

    // 수정: DB 갱신이 멈춰도 로컬 단조 시계로 TTL을 감시하여 실행기가 종료 절차로 들어간다.
    public final class Guard implements AutoCloseable {
        private volatile boolean lost;
        private volatile boolean closed;
        private volatile long validUntil;
        private final LeaseClaim claim;
        private final ScheduledFuture<?> task;
        private volatile Map<String, String> logContext = MDC.getCopyOfContextMap();

        public void captureContext() { logContext = MDC.getCopyOfContextMap(); }

        private Guard(LeaseClaim claim) {
            this.claim = claim;
            pulse();
            long interval = properties.heartbeatInterval().toMillis();
            task = heartbeats.scheduleWithFixedDelay(this::pulse, interval, interval, TimeUnit.MILLISECONDS);
        }

        private void pulse() {
            try (var trace = ExecutionLogContext.restore(logContext)) { pulseWithContext(); }
        }

        private void pulseWithContext() {
            if (closed || lost) return;
            long beforeRequest = System.nanoTime();
            try {
                coordinator.heartbeat(claim);
                validUntil = beforeRequest + guardTtlNanos;
            } catch (RuntimeException failure) {
                lost = true;
                log.warn("Lease heartbeat failed for attempt {}", claim.attemptId(), failure);
            }
        }

        public boolean valid() { return !closed && !lost && System.nanoTime() < validUntil; }

        @Override
        public void close() {
            closed = true;
            task.cancel(false);
        }
    }
}
