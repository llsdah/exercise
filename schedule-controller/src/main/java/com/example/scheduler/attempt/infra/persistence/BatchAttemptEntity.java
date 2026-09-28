package com.example.scheduler.attempt.infra.persistence;

import com.example.scheduler.execution.domain.ExecutionStatus;

import com.example.scheduler.attempt.domain.BatchAttempt;
import com.example.scheduler.attempt.domain.AttemptState;
import com.example.scheduler.attempt.domain.ProcessLaunchState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

// 수정: 실제 실행 시도별 소유 노드, PID, 토큰, 종료 결과를 저장한다.
@Entity
@Table(name = "BATCH_ATTEMPT", uniqueConstraints = @UniqueConstraint(columnNames = {"EXECUTION_ID", "ATTEMPT_NO"}))
@Getter
@NoArgsConstructor
public class BatchAttemptEntity {
    public static BatchAttemptEntity starting(String executionId, int attemptNo, String nodeId, long token, Instant now) {
        var attempt = new BatchAttemptEntity();
        attempt.id = executionId + "-" + attemptNo;
        attempt.executionId = executionId;
        attempt.attemptNo = attemptNo;
        attempt.nodeId = nodeId;
        attempt.leaseToken = token;
        attempt.startedAt = now;
        attempt.heartbeatAt = now;
        attempt.status = AttemptState.STARTING;
        attempt.processLaunchState = ProcessLaunchState.NOT_REQUESTED;
        return attempt;
    }

    public void started(long pid, Instant processStartedAt) {
        this.workerNodeId = nodeId;
        this.pid = pid;
        this.processStartedAt = processStartedAt;
        status = AttemptState.RUNNING;
        processLaunchState = ProcessLaunchState.STARTED;
    }

    public void launchRequested() { processLaunchState = ProcessLaunchState.START_REQUESTED; }

    public void startFailed(String reason, Instant now, boolean startInvoked) {
        if (startInvoked) workerNodeId = nodeId;
        processLaunchState = ProcessLaunchState.START_FAILED;
        finish(com.example.scheduler.execution.domain.ExecutionStatus.FAILURE, null, reason, now);
    }

    public void heartbeat(Instant now) { heartbeatAt = now; }

    public void cancelledBeforeStart(Instant now) {
        processLaunchState = ProcessLaunchState.NOT_STARTED;
        finish(ExecutionStatus.CANCELLED, null, "Cancelled before process start", now);
    }

    public void deferredByDrain(Instant now) {
        status = AttemptState.DEFERRED;
        processLaunchState = ProcessLaunchState.NOT_STARTED;
        endedAt = now;
        // No OS invocation, worker, exit code or failure. Keep the admission audit and token.
    }

    public void finish(com.example.scheduler.execution.domain.ExecutionStatus outcome, Integer exitCode, String reason, Instant now) {
        status = AttemptState.resultOf(outcome);
        this.exitCode = exitCode;
        failureReason = reason == null ? null : reason.substring(0, Math.min(1000, reason.length()));
        endedAt = now;
    }

    public void expire(String reason) {
        status = AttemptState.UNKNOWN;
        failureReason = reason;
        // The process may still exist on a failed node. Do not invent its end time.
    }
    @Id @Column(name = "ATTEMPT_ID", length = 64) String id;
    @Column(name = "EXECUTION_ID", nullable = false, length = 36) String executionId;
    @Column(name = "ATTEMPT_NO", nullable = false) int attemptNo;
    @Column(name = "NODE_ID", nullable = false, length = 100) String nodeId;
    @Column(name = "LEASE_TOKEN", nullable = false) long leaseToken;
    @Column(name = "WORKER_NODE_ID", length = 100) String workerNodeId;
    @Column(name = "PID") Long pid;
    @Column(name = "STARTED_AT", nullable = false) Instant startedAt;
    @Column(name = "PROCESS_STARTED_AT") Instant processStartedAt;
    @Column(name = "ENDED_AT") Instant endedAt;
    @Column(name = "EXIT_CODE") Integer exitCode;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false) AttemptState status;
    @Column(name = "FAILURE_REASON", length = 1000) String failureReason;
    @Column(name = "HEARTBEAT_AT") Instant heartbeatAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "PROCESS_LAUNCH_STATE", length = 20, nullable = false)
    ProcessLaunchState processLaunchState = ProcessLaunchState.UNKNOWN;

    // 수정: Attempt 조회는 영속 엔티티 대신 도메인 모델을 반환한다.
    public BatchAttempt toDomain() {
        return new BatchAttempt(id, executionId, attemptNo, nodeId, leaseToken, pid,
                startedAt, processStartedAt, endedAt, exitCode, status, failureReason, heartbeatAt, processLaunchState, workerNodeId);
    }
}
