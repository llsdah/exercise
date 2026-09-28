package com.example.scheduler.resource.application.capacity;

import com.example.scheduler.global.config.NodeCapacityProperties;
import com.example.scheduler.system.application.SystemMonitorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Optional;

/** One singleton per Scheduler JVM. Reservations include processes being prepared. */
@Component
@EnableConfigurationProperties(NodeCapacityProperties.class)
public class NodeExecutionCapacity {
    @FunctionalInterface
    public interface LaunchFence {
        Process start(com.example.scheduler.execution.application.port.ProcessStarter invocation) throws java.io.IOException;
    }
    public interface Metrics { double cpuLoad(); long availableMemoryBytes(); }
    private final NodeCapacityProperties policy;
    private final java.util.function.Supplier<SystemMonitorService.Snapshot> measurements;
    private final com.example.scheduler.node.application.NodeRegistry nodes;
    private int reserved;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.scheduler.metrics.BatchMetrics batchMetrics;

    @org.springframework.beans.factory.annotation.Autowired
    public NodeExecutionCapacity(NodeCapacityProperties policy, SystemMonitorService monitor,
                                 com.example.scheduler.node.application.NodeRegistry nodes) {
        this.policy = policy;
        this.measurements = monitor::snapshot;
        this.nodes = nodes;
    }

    /** Deterministic measurement seam; production always uses SystemMonitorService. */
    public NodeExecutionCapacity(NodeCapacityProperties policy, Metrics metrics) {
        this.policy = policy;
        this.nodes = null;
        this.measurements = () -> new SystemMonitorService.Snapshot(metrics.cpuLoad(), metrics.availableMemoryBytes(), -1);
    }

    public synchronized Optional<Slot> tryAcquire() {
        if (reserved >= policy.maxConcurrentProcesses()) {
            rejected(com.example.scheduler.metrics.BatchMetrics.Rejection.PROCESS_LIMIT);
            return Optional.empty();
        }
        try {
            if (nodes != null && !nodes.isLocalActive()) return Optional.empty();
            var snapshot = measurements.get();
            double cpu = snapshot.cpuLoad();
            long memory = snapshot.availableMemoryBytes();
            // Unknown metrics fail closed; they do not grant an execution right.
            if (!Double.isFinite(cpu) || cpu < 0 || cpu >= policy.cpuThreshold()) {
                rejected(com.example.scheduler.metrics.BatchMetrics.Rejection.CPU);
                return Optional.empty();
            }
            if (memory < 0 || memory < policy.minAvailableMemoryBytes()) {
                rejected(com.example.scheduler.metrics.BatchMetrics.Rejection.MEMORY);
                return Optional.empty();
            }
        } catch (RuntimeException unavailable) { return Optional.empty(); }
        reserved++;
        return Optional.of(new Slot());
    }

    private void rejected(com.example.scheduler.metrics.BatchMetrics.Rejection reason) {
        if (batchMetrics != null) batchMetrics.rejected(reason);
    }

    public void requireLocalSlot(Slot slot) {
        if (slot == null || slot.owner() != this) throw new IllegalArgumentException("Slot belongs to another Node capacity");
    }

    public synchronized int reservedSlots() { return reserved; }

    public final class Slot implements AutoCloseable {
        private Process process;
        private boolean released;
        private boolean attached;
        private boolean startInvoked;
        public synchronized boolean startInvoked() { return startInvoked; }
        private Slot() { }
        private NodeExecutionCapacity owner() { return NodeExecutionCapacity.this; }
        public synchronized Process start(ProcessBuilder builder) throws java.io.IOException {
            return start(builder, () -> { });
        }
        public synchronized Process start(ProcessBuilder builder, Runnable beforeInvocation) throws java.io.IOException {
            return start(builder, beforeInvocation, invocation -> invocation.start());
        }
        public synchronized Process start(ProcessBuilder builder, Runnable beforeInvocation, LaunchFence fence) throws java.io.IOException {
            if (released || attached) throw new IllegalStateException("Slot is no longer available for a process");
            com.example.scheduler.execution.application.port.ProcessStarter invoke = () -> fence.start(() -> {
                beforeInvocation.run();
                startInvoked = true;
                return builder.start();
            });
            if (nodes == null) {
                this.process = invoke.start();
            } else {
                this.process = nodes.start(invoke);
            }
            attached = true;
            // Also returns a retained slot when termination could not be confirmed by the executor.
            process.onExit().thenRun(this::close);
            return process;
        }
        @Override public synchronized void close() {
            if (released || (process != null && process.isAlive())) return;
            released = true;
            synchronized (NodeExecutionCapacity.this) { reserved--; }
        }
    }
}
