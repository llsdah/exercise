package com.example.scheduler.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.UUID;

// 수정: TTL/갱신 간격을 검증하고 노드 재시작마다 새로운 소유자 식별자를 사용한다.
@ConfigurationProperties("app.execution")
public record ExecutionProperties(String nodeId, Duration leaseTtl, Duration heartbeatInterval) {
    public ExecutionProperties {
        nodeId = nodeId == null || nodeId.isBlank() ? UUID.randomUUID().toString() : nodeId;
        leaseTtl = leaseTtl == null ? Duration.ofSeconds(30) : leaseTtl;
        heartbeatInterval = heartbeatInterval == null ? Duration.ofSeconds(10) : heartbeatInterval;
        if (nodeId.length() > 100 || leaseTtl.toMillis() < 3 || heartbeatInterval.toMillis() < 1
                || heartbeatInterval.multipliedBy(2).compareTo(leaseTtl) >= 0) {
            throw new IllegalArgumentException("Node ID <= 100 chars; heartbeat interval must be positive and < lease TTL / 2");
        }
    }
}
