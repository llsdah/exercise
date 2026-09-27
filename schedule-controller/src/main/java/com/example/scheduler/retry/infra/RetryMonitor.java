package com.example.scheduler.retry.infra;
import com.example.scheduler.global.config.RetryProperties;
import com.example.scheduler.retry.application.RetryService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;
@Component
@Slf4j
@ConditionalOnProperty(name = "app.retry.enabled", havingValue = "true", matchIfMissing = true)
public class RetryMonitor {
    private final RetryService service;
    private final ThreadPoolExecutor workers;
    public RetryMonitor(RetryService service, RetryProperties policy) {
        this.service = service;
        // No queued acquired leases; process waits never block the scheduler's lease sweep thread.
        workers = new ThreadPoolExecutor(policy.workers(), policy.workers(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<>(), Thread.ofVirtual().name("execution-retry-", 0).factory());
    }
    @Scheduled(fixedDelayString = "${app.retry.sweep-interval-ms:5000}")
    public void sweep() {
        try {
            for (var candidate : service.candidates()) {
                try {
                    workers.execute(() -> {
                        try { service.retry(candidate.executionId(), candidate.attemptId()); }
                        catch (RuntimeException failure) {
                            log.warn("Retry failed for {}; lease recovery handles any acquired attempt", candidate.executionId(), failure);
                        }
                    });
                } catch (RejectedExecutionException busy) { break; }
            }
        } catch (RuntimeException failure) { log.warn("Retry scan unavailable", failure); }
    }
    @PreDestroy
    public void shutdown() { workers.shutdownNow(); }
}
