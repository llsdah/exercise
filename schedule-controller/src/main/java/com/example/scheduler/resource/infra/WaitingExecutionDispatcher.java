package com.example.scheduler.resource.infra;

import com.example.scheduler.resource.application.WaitingExecutionService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;

@Component
@Slf4j
public class WaitingExecutionDispatcher {
    private final WaitingExecutionService waiting;
    private final boolean enabled;
    // Local slots bound process admission; acquired leases are never queued.
    private final ExecutorService workers = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("waiting-dispatch-", 0).factory());

    public WaitingExecutionDispatcher(WaitingExecutionService waiting,
            @Value("${app.node-capacity.waiting-dispatch-enabled:true}") boolean enabled) {
        this.waiting = waiting; this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${app.node-capacity.dispatch-interval-ms:1000}")
    public synchronized void dispatch() {
        if (!enabled || workers.isShutdown()) return;
        try {
            for (var candidate : waiting.candidates()) {
                if (!waiting.dispatch(candidate, workers)) break;
            }
        } catch (RuntimeException failure) { log.warn("WAITING dispatch unavailable", failure); }
    }

    @PreDestroy public synchronized void shutdown() { workers.shutdownNow(); }
}
