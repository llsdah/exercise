package com.example.scheduler.node.application;

import com.example.scheduler.global.config.*;
import com.example.scheduler.global.infra.DatabaseClock;
import com.example.scheduler.node.domain.*;
import com.example.scheduler.node.infra.persistence.SchedulerNodeEntity;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import java.io.IOException;
import java.util.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;

/** Node admission is independent of execution lease renewal and process lifetime. */
@Service
@EnableConfigurationProperties({NodeProperties.class, ExecutionProperties.class})
public class NodeRegistry {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NodeRegistry.class);
    @PersistenceContext private EntityManager em;
    private final ExecutionProperties execution;
    private final NodeProperties policy;
    private final DatabaseClock clock;
    private final TransactionTemplate transaction;
    private final String instanceId = UUID.randomUUID().toString();
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.scheduler.metrics.BatchMetrics batchMetrics;

    public NodeRegistry(ExecutionProperties execution, NodeProperties policy, DatabaseClock clock,
                        PlatformTransactionManager manager) {
        this.execution = execution; this.policy = policy; this.clock = clock;
        transaction = new TransactionTemplate(manager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transaction.setTimeout(5);
    }

    @PostConstruct public void register() {
        transaction.executeWithoutResult(tx -> {
            var node = lock(execution.nodeId());
            var now = clock.now();
            if (node == null) {
                node = new SchedulerNodeEntity(execution.nodeId());
                node.register(instanceId, now); em.persist(node);
            } else {
                if (node.fresh(now, policy.offlineTimeout()) && !node.belongsTo(instanceId))
                    throw new IllegalStateException("Node ID is already live: " + execution.nodeId());
                node.register(instanceId, now);
            }
        });
    }

    public boolean isLocalActive() {
        return Boolean.TRUE.equals(transaction.execute(tx -> {
            var node = em.find(SchedulerNodeEntity.class, execution.nodeId());
            var now = node != null && node.belongsTo(instanceId) ? clock.now() : null;
            boolean active = now != null && node.active(now, policy.offlineTimeout());
            if (!active) recordDrainRejection(node, now);
            return active;
        }));
    }

    /** Held until the caller's Claim transaction commits. Always lock Node before History. */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean lockActive(String nodeId) {
        if (!execution.nodeId().equals(nodeId)) return false;
        var node = lock(nodeId);
        var now = node != null && node.belongsTo(instanceId) ? clock.now() : null;
        boolean active = now != null && node.active(now, policy.offlineTimeout());
        if (!active) recordDrainRejection(node, now);
        return active;
    }

    /** Drain and the actual OS invocation serialize on the same DB row. */
    public Process start(ProcessBuilder builder, Runnable beforeInvocation) throws IOException {
        return start(() -> { beforeInvocation.run(); return builder.start(); });
    }

    public Process start(com.example.scheduler.execution.application.port.ProcessStarter starter) throws IOException {
        Process[] started = new Process[1];
        IOException[] failed = new IOException[1];
        try {
            transaction.executeWithoutResult(tx -> {
                var node = lock(execution.nodeId());
                var now = clock.now();
                if (node != null && node.belongsTo(instanceId)
                        && node.view(now, policy.offlineTimeout()).status() == NodeStatus.DRAINING) {
                    recordDrainRejection(node, now);
                    throw new NodeDrainingException(execution.nodeId());
                }
                if (node == null || !node.belongsTo(instanceId) || !node.active(now, policy.offlineTimeout()))
                    throw new IllegalStateException("Node is not ACTIVE; process not started");
                try { started[0] = starter.start(); }
                catch (IOException failure) { failed[0] = failure; }
            });
        } catch (RuntimeException failure) {
            // OS start cannot roll back. Preserve its handle even if releasing the DB lock fails.
            if (started[0] == null) throw failure;
            log.error("Node admission transaction failed after OS start; retaining process handle pid={}", started[0].pid(), failure);
        }
        if (failed[0] != null) throw failed[0];
        return started[0];
    }

    public Optional<NodeView> setDrain(String nodeId, boolean drain) {
        return transaction.execute(tx -> {
            var node = lock(nodeId);
            if (node == null) return Optional.empty();
            var now = clock.now();
            node.drain(drain, node.fresh(now, policy.offlineTimeout()));
            return Optional.of(node.view(now, policy.offlineTimeout()));
        });
    }
    public List<NodeView> list() {
        return transaction.execute(tx -> {
            var now = clock.now();
            return em.createQuery("select n from SchedulerNodeEntity n order by n.nodeId", SchedulerNodeEntity.class)
                    .getResultStream().map(n -> n.view(now, policy.offlineTimeout())).toList();
        });
    }

    @Scheduled(fixedDelayString = "${app.node.heartbeat-interval:5s}")
    public void heartbeat() {
        try {
            transaction.executeWithoutResult(tx -> {
                var node = lock(execution.nodeId());
                if (node != null && node.belongsTo(instanceId)) node.heartbeat(clock.now());
            });
        } catch (RuntimeException failure) { log.warn("Node heartbeat failed: {}", execution.nodeId(), failure); }
    }

    @Scheduled(fixedDelayString = "${app.node.sweep-interval:5s}")
    public void expireNodes() {
        try {
            var ids = transaction.execute(tx -> em.createQuery(
                    "select n.nodeId from SchedulerNodeEntity n where n.lastHeartbeat <= :cutoff and n.status <> :offline", String.class)
                    .setParameter("cutoff", clock.now().minus(policy.offlineTimeout()))
                    .setParameter("offline", NodeStatus.OFFLINE).getResultList());
            for (String id : ids) transaction.executeWithoutResult(tx -> {
                var node = lock(id);
                if (node != null && !node.fresh(clock.now(), policy.offlineTimeout())) node.offline();
            });
        } catch (RuntimeException failure) { log.warn("Node expiry scan failed", failure); }
    }
    private SchedulerNodeEntity lock(String id) {
        return em.find(SchedulerNodeEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    private void recordDrainRejection(SchedulerNodeEntity node, java.time.Instant now) {
        try {
            if (batchMetrics != null && now != null && node != null && node.belongsTo(instanceId)
                    && node.view(now, policy.offlineTimeout()).status() == NodeStatus.DRAINING)
                batchMetrics.rejected(com.example.scheduler.metrics.BatchMetrics.Rejection.DRAINING);
        } catch (RuntimeException ignored) { /* Observability cannot affect Node admission. */ }
    }
}
