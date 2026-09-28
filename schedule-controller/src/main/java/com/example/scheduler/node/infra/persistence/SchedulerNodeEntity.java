package com.example.scheduler.node.infra.persistence;

import com.example.scheduler.node.domain.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "BATCH_NODE")
public class SchedulerNodeEntity {
    @Id @Column(name = "NODE_ID", length = 100) private String nodeId;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private NodeStatus status;
    @Column(name = "LAST_HEARTBEAT", nullable = false) private Instant lastHeartbeat;
    @Column(name = "STARTED_AT", nullable = false) private Instant startedAt;
    @Column(name = "INSTANCE_ID", nullable = false, length = 36) private String instanceId;
    @Column(name = "DRAIN_REQUESTED", nullable = false) private boolean drainRequested;
    protected SchedulerNodeEntity() { }
    public SchedulerNodeEntity(String nodeId) { this.nodeId = nodeId; }
    public boolean belongsTo(String instance) { return instance.equals(instanceId); }
    public boolean fresh(Instant now, java.time.Duration timeout) {
        return lastHeartbeat != null && lastHeartbeat.plus(timeout).isAfter(now);
    }
    public void register(String instance, Instant now) {
        instanceId = instance; startedAt = now; heartbeat(now);
    }
    public void heartbeat(Instant now) {
        lastHeartbeat = now; status = drainRequested ? NodeStatus.DRAINING : NodeStatus.ACTIVE;
    }
    public void offline() { status = NodeStatus.OFFLINE; }
    public void drain(boolean drain, boolean fresh) {
        drainRequested = drain;
        status = fresh ? (drain ? NodeStatus.DRAINING : NodeStatus.ACTIVE) : NodeStatus.OFFLINE;
    }
    public boolean active(Instant now, java.time.Duration timeout) {
        return status == NodeStatus.ACTIVE && fresh(now, timeout);
    }
    public NodeView view(Instant now, java.time.Duration timeout) {
        return new NodeView(nodeId, fresh(now, timeout) ? status : NodeStatus.OFFLINE,
                lastHeartbeat, startedAt, drainRequested);
    }
}
