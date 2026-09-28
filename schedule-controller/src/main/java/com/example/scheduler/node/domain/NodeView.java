package com.example.scheduler.node.domain;

import java.time.Instant;

public record NodeView(String nodeId, NodeStatus status, Instant lastHeartbeat, Instant startedAt,
                       boolean drainRequested) { }
