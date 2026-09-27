package com.example.scheduler.lease.infra.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

// 수정: 실행권과 단조 증가 토큰을 DB에 보존하여 JVM 재시작에도 fencing을 유지한다.
@Entity
@Table(name = "BATCH_EXECUTION_LEASE")
@Getter
@NoArgsConstructor
public class ExecutionLeaseEntity {
    public static ExecutionLeaseEntity unowned(String executionId) {
        var lease = new ExecutionLeaseEntity();
        lease.executionId = executionId;
        lease.expiresAt = Instant.EPOCH;
        return lease;
    }

    public void acquire(String node, Instant expiry) {
        ownerNode = node;
        leaseToken++;
        expiresAt = expiry;
    }

    public void renew(Instant expiry) { expiresAt = expiry; }

    public void release(Instant now) {
        ownerNode = null;
        expiresAt = now;
    }

    public void expire(Instant now) {
        leaseToken++;
        release(now);
    }
    @Id @Column(name = "EXECUTION_ID", length = 36) String executionId;
    @Column(name = "OWNER_NODE", length = 100) String ownerNode;
    @Column(name = "LEASE_TOKEN", nullable = false) long leaseToken;
    @Column(name = "EXPIRES_AT", nullable = false) Instant expiresAt;
    @Version @Column(name = "VERSION") long version;
}
