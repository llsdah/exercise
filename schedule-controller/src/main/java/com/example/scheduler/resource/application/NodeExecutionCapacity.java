package com.example.scheduler.resource.application;

import com.example.scheduler.global.config.NodeCapacityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Optional;

/** One singleton per Scheduler JVM. Reservations include processes being prepared. */
@Component
@EnableConfigurationProperties(NodeCapacityProperties.class)
public class NodeExecutionCapacity {
    public interface Metrics { double cpuLoad(); long availableMemoryBytes(); }
    private final NodeCapacityProperties policy;
    private final Metrics metrics;
    private int reserved;

    @org.springframework.beans.factory.annotation.Autowired
    public NodeExecutionCapacity(NodeCapacityProperties policy) {
        this(policy, new Metrics() {
            private final com.sun.management.OperatingSystemMXBean os =
                    (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            public double cpuLoad() { return os.getCpuLoad(); }
            public long availableMemoryBytes() { return os.getFreeMemorySize(); }
        });
    }

    public NodeExecutionCapacity(NodeCapacityProperties policy, Metrics metrics) {
        this.policy = policy; this.metrics = metrics;
    }

    public synchronized Optional<Slot> tryAcquire() {
        if (reserved >= policy.maxConcurrentProcesses()) return Optional.empty();
        try {
            double cpu = metrics.cpuLoad();
            long memory = metrics.availableMemoryBytes();
            // Unknown metrics fail closed; they do not grant an execution right.
            if (!Double.isFinite(cpu) || cpu < 0 || cpu >= policy.cpuThreshold()
                    || memory < 0 || memory < policy.minAvailableMemoryBytes()) return Optional.empty();
        } catch (RuntimeException unavailable) { return Optional.empty(); }
        reserved++;
        return Optional.of(new Slot());
    }

    public void requireLocalSlot(Slot slot) {
        if (slot == null || slot.owner() != this) throw new IllegalArgumentException("Slot belongs to another Node capacity");
    }

    public synchronized int reservedSlots() { return reserved; }

    public final class Slot implements AutoCloseable {
        private Process process;
        private boolean released;
        private boolean attached;
        private Slot() { }
        private NodeExecutionCapacity owner() { return NodeExecutionCapacity.this; }
        public synchronized Process start(ProcessBuilder builder) throws java.io.IOException {
            if (released || attached) throw new IllegalStateException("Slot is no longer available for a process");
            this.process = builder.start();
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
