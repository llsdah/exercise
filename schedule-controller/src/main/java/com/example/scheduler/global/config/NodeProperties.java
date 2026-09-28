package com.example.scheduler.global.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.node")
public record NodeProperties(Duration heartbeatInterval, Duration offlineTimeout) {
    public NodeProperties {
        heartbeatInterval = heartbeatInterval == null ? Duration.ofSeconds(5) : heartbeatInterval;
        offlineTimeout = offlineTimeout == null ? Duration.ofSeconds(30) : offlineTimeout;
        if (heartbeatInterval.toMillis() < 1 || offlineTimeout.compareTo(heartbeatInterval.multipliedBy(2)) <= 0)
            throw new IllegalArgumentException("Node offline timeout must exceed twice its positive heartbeat interval");
    }
}
