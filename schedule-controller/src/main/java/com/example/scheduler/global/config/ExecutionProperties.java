package com.example.scheduler.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.UUID;

// Stable node identity is required; only explicitly opted-in local runs may generate one.
@ConfigurationProperties("app.execution")
public record ExecutionProperties(String nodeId, Duration leaseTtl, Duration heartbeatInterval,
                                  boolean allowEphemeralNodeId) implements org.springframework.context.EnvironmentAware {
    public ExecutionProperties(String nodeId, Duration leaseTtl, Duration heartbeatInterval) {
        this(nodeId, leaseTtl, heartbeatInterval, false);
    }

    @org.springframework.boot.context.properties.bind.ConstructorBinding
    public ExecutionProperties {
        if (nodeId == null || nodeId.isBlank()) {
            if (!allowEphemeralNodeId) throw new IllegalArgumentException("app.execution.node-id (BATCH_NODE_ID) is required");
            nodeId = UUID.randomUUID().toString();
        }
        leaseTtl = leaseTtl == null ? Duration.ofSeconds(30) : leaseTtl;
        heartbeatInterval = heartbeatInterval == null ? Duration.ofSeconds(10) : heartbeatInterval;
        if (nodeId.length() > 100 || leaseTtl.toMillis() < 3 || heartbeatInterval.toMillis() < 1
                || heartbeatInterval.multipliedBy(2).compareTo(leaseTtl) >= 0) {
            throw new IllegalArgumentException("Node ID <= 100 chars; heartbeat interval must be positive and < lease TTL / 2");
        }
    }

    @Override
    public void setEnvironment(org.springframework.core.env.Environment environment) {
        if (allowEphemeralNodeId && !environment.matchesProfiles("local & !production & !prod"))
            throw new IllegalArgumentException("allow-ephemeral-node-id is only permitted in the local profile");
    }
}
