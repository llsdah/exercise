package com.example.scheduler.metrics;

import com.example.scheduler.execution.domain.ExecutionStatus;
import com.example.scheduler.global.config.NodeCapacityProperties;
import com.example.scheduler.job.infra.executor.JobProcessManager;
import com.example.scheduler.system.application.SystemMonitorService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;

/** Best-effort instrumentation only; never grants or revokes execution rights. */
@Component
public class BatchMetrics {
    public enum Rejection { CPU, MEMORY, PROCESS_LIMIT, DRAINING }

    private final MeterRegistry registry;
    private final SystemMonitorService monitor;
    private final JobProcessManager processes;
    private final NodeCapacityProperties capacity;
    private final TransactionTemplate reads;
    private final EnumMap<Rejection, Counter> rejections = new EnumMap<>(Rejection.class);
    private Counter retries;
    private volatile Counts counts = new Counts(Double.NaN, Double.NaN);
    private ScheduledExecutorService sampler;
    @PersistenceContext private EntityManager em;

    private record Counts(double waiting, double unknown) { }

    public BatchMetrics(MeterRegistry registry, SystemMonitorService monitor, JobProcessManager processes,
                        NodeCapacityProperties capacity, PlatformTransactionManager transactions) {
        this.registry = registry;
        this.monitor = monitor;
        this.processes = processes;
        this.capacity = capacity;
        reads = new TransactionTemplate(transactions);
        reads.setReadOnly(true);
        reads.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        reads.setTimeout(2);
    }

    @PostConstruct
    public void initialize() {
        gauge("batch.node.cpu.usage", "Last existing node CPU sample, 0..1", () -> {
            double cpu = monitor.latestSnapshot().cpuLoad();
            return Double.isFinite(cpu) && cpu >= 0 && cpu <= 1 ? cpu : Double.NaN;
        });
        gauge("batch.node.memory.usage", "Last existing physical memory sample, 0..1", () -> {
            var sample = monitor.latestSnapshot();
            return sample.totalMemoryBytes() > 0 && sample.availableMemoryBytes() >= 0
                    && sample.availableMemoryBytes() <= sample.totalMemoryBytes()
                    ? 1.0 - (double) sample.availableMemoryBytes() / sample.totalMemoryBytes() : Double.NaN;
        });
        gauge("batch.node.process.running", "Live registered external batch processes on this node",
                () -> processes.getRunningProcesses().values().stream().filter(info -> info.getProcess().isAlive()).count());
        gauge("batch.node.process.limit", "Configured concurrent process limit on this node", capacity::maxConcurrentProcesses);
        gauge("batch.execution.waiting", "Shared database WAITING count sampled every 60 seconds", () -> counts.waiting());
        gauge("batch.execution.unknown", "Shared database UNKNOWN count sampled every 60 seconds", () -> counts.unknown());
        for (var reason : Rejection.values()) {
            try { rejections.put(reason, Counter.builder("batch.admission.rejected").tag("reason", reason.name()).register(registry)); }
            catch (RuntimeException ignored) { /* Registry failure must not disable batch processing. */ }
        }
        try { retries = Counter.builder("batch.execution.retry").description("Committed retry attempts created by this node").register(registry); }
        catch (RuntimeException ignored) { }
        try {
            sampler = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().daemon().name("batch-metrics").factory());
            // Separate from lease renewal, node heartbeat and retry schedulers; no DB access during scrape.
            sampler.scheduleWithFixedDelay(this::refreshCounts, 0, 60, TimeUnit.SECONDS);
        } catch (RuntimeException ignored) { }
    }

    public void rejected(Rejection reason) {
        try { var counter = rejections.get(reason); if (counter != null) counter.increment(); }
        catch (RuntimeException ignored) { }
    }

    public void retryCreated() {
        try { if (retries != null) retries.increment(); }
        catch (RuntimeException ignored) { }
    }

    private void gauge(String name, String description, DoubleSupplier value) {
        try {
            Gauge.builder(name, value, source -> {
                try { return source.getAsDouble(); }
                catch (RuntimeException unavailable) { return Double.NaN; }
            }).description(description).strongReference(true).register(registry);
        } catch (RuntimeException ignored) { }
    }

    private void refreshCounts() {
        try {
            Counts refreshed = reads.execute(tx -> {
                // STATUS is the leading column of the existing IX_HISTORY_WAIT_FIFO index.
                var rows = em.createQuery("select h.status, count(h) from JobExecutionHistoryEntity h "
                                + "where h.status in :states group by h.status", Object[].class)
                        .setParameter("states", List.of(ExecutionStatus.WAITING, ExecutionStatus.UNKNOWN))
                        .setHint("jakarta.persistence.query.timeout", 2000).getResultList();
                double waiting = 0, unknown = 0;
                for (var row : rows) {
                    if (row[0] == ExecutionStatus.WAITING) waiting = ((Number) row[1]).doubleValue();
                    else if (row[0] == ExecutionStatus.UNKNOWN) unknown = ((Number) row[1]).doubleValue();
                }
                return new Counts(waiting, unknown);
            });
            if (refreshed != null) counts = refreshed;
        } catch (RuntimeException unavailable) { counts = new Counts(Double.NaN, Double.NaN); }
    }

    @PreDestroy
    public void close() {
        try { if (sampler != null) sampler.shutdownNow(); }
        catch (RuntimeException ignored) { }
    }
}
