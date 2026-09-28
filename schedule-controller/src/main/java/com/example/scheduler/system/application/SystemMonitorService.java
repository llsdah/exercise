package com.example.scheduler.system.application;

import org.springframework.stereotype.Service;
import java.lang.management.ManagementFactory;

/** Single source for node OS measurements; execution admission belongs to NodeExecutionCapacity. */
@Service
public class SystemMonitorService {
    public record Snapshot(double cpuLoad, long availableMemoryBytes, long totalMemoryBytes) { }

    private final com.sun.management.OperatingSystemMXBean os;
    private volatile Snapshot latest = new Snapshot(Double.NaN, -1, -1);

    public SystemMonitorService() {
        var bean = ManagementFactory.getOperatingSystemMXBean();
        os = bean instanceof com.sun.management.OperatingSystemMXBean supported ? supported : null;
    }

    /** CPU is 0..1; memory is physical bytes, not JVM heap. No sampling sleep. */
    public Snapshot snapshot() {
        if (os == null) return new Snapshot(Double.NaN, -1, -1);
        var sample = new Snapshot(os.getCpuLoad(), os.getFreeMemorySize(), os.getTotalMemorySize());
        latest = sample;
        return sample;
    }

    /** Observation only: does not trigger another OS measurement. */
    public Snapshot latestSnapshot() { return latest; }

    /** Legacy monitoring thresholds; the optional Quartz veto listener remains unregistered. */
    public boolean isSystemOverloaded() {
        var metrics = snapshot();
        if (!Double.isFinite(metrics.cpuLoad()) || metrics.cpuLoad() < 0
                || metrics.availableMemoryBytes() < 0 || metrics.totalMemoryBytes() <= 0) return true;
        double memoryUsage = 1.0 - (double) metrics.availableMemoryBytes() / metrics.totalMemoryBytes();
        return metrics.cpuLoad() > 0.80 || memoryUsage > 0.90;
    }
}
