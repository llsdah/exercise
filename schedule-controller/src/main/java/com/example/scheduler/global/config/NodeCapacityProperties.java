package com.example.scheduler.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.node-capacity")
public record NodeCapacityProperties(Integer maxConcurrentProcesses, Double cpuThreshold, Long minAvailableMemoryBytes) {
    public NodeCapacityProperties {
        maxConcurrentProcesses = maxConcurrentProcesses == null ? 2 : maxConcurrentProcesses;
        cpuThreshold = cpuThreshold == null ? 0.85 : cpuThreshold;
        minAvailableMemoryBytes = minAvailableMemoryBytes == null ? 536870912L : minAvailableMemoryBytes;
        if (maxConcurrentProcesses < 1 || !Double.isFinite(cpuThreshold) || cpuThreshold <= 0 || cpuThreshold > 1
                || minAvailableMemoryBytes < 0) throw new IllegalArgumentException("Invalid node execution capacity settings");
    }
}
