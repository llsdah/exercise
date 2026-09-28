package com.example.scheduler.dependency.infra.persistence;

import com.example.scheduler.dependency.domain.DependencyNames;
import com.example.scheduler.dependency.domain.InvalidDependencyException;
import com.example.scheduler.job.infra.persistence.JobEntity;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.*;

/** Serializes registration graph changes across nodes, including previously absent Job rows. */
@Component
public class DependencyRegistrationGuard {
    @PersistenceContext private EntityManager em;
    private final TransactionTemplate seed;
    public DependencyRegistrationGuard(PlatformTransactionManager manager) {
        seed = new TransactionTemplate(manager);
        seed.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }
    @PostConstruct public void initialize() {
        try { seed.executeWithoutResult(tx -> {
            if (em.find(DependencyGraphLock.class, "GLOBAL") == null) {
                em.persist(new DependencyGraphLock("GLOBAL")); em.flush();
            }
        }); } catch (RuntimeException competingInitializer) {
            if (!Boolean.TRUE.equals(seed.execute(tx -> em.find(DependencyGraphLock.class, "GLOBAL") != null))) throw competingInitializer;
        }
    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void lock() {
        if (em.find(DependencyGraphLock.class, "GLOBAL", LockModeType.PESSIMISTIC_WRITE) == null)
            throw new IllegalStateException("Dependency graph lock is missing");
    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void validate(String tenant, String group, String name, String scheduleType, List<String> proposed) {
        // The caller acquired lock() before reading or updating the Job.
        Map<String,List<String>> graph = new HashMap<>();
        Map<String,String> types = new HashMap<>();
        for (var job : em.createQuery("select j from JobEntity j where j.id.tenantId = :tenant and j.id.scheduleGroup = :group", JobEntity.class)
                .setParameter("tenant",tenant).setParameter("group",group).getResultList()) {
            graph.put(job.getId().getScheduleName(), DependencyNames.decode(job.getDependsOn()));
            types.put(job.getId().getScheduleName(), job.getScheduleType());
        }
        var parents = DependencyNames.normalize(proposed);
        for (String parent : parents) {
            if (name.equals(parent))
                throw new InvalidDependencyException("Cyclic dependsOn registration: job cannot depend on itself: " + name);
            if (!types.containsKey(parent))
                throw new InvalidDependencyException("Predecessor does not exist in the same tenant and schedule group: " + parent);
        }
        graph.put(name, parents);
        types.put(name, scheduleType);
        for (var entry : graph.entrySet()) {
            if (!entry.getValue().isEmpty() && !"CRON".equals(types.get(entry.getKey())))
                throw new InvalidDependencyException("Only CRON jobs can register dependsOn: " + entry.getKey());
            for (String parent : entry.getValue()) {
                if (types.containsKey(parent) && !"CRON".equals(types.get(parent)))
                    throw new InvalidDependencyException("Predecessor must be a CRON job: " + parent);
            }
        }
        var done = new HashSet<String>(); var active = new HashSet<String>();
        for (String node : graph.keySet()) visit(node, graph, done, active);
    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void validateDeletion(String tenant, String group, String name) {
        for (var job : em.createQuery("select j from JobEntity j where j.id.tenantId = :tenant and j.id.scheduleGroup = :group", JobEntity.class)
                .setParameter("tenant", tenant).setParameter("group", group).getResultList()) {
            if (!name.equals(job.getId().getScheduleName()) && DependencyNames.decode(job.getDependsOn()).contains(name))
                throw new InvalidDependencyException("Job is referenced by dependsOn: " + job.getId().getScheduleName());
        }
    }
    private void visit(String node, Map<String,List<String>> graph, Set<String> done, Set<String> active) {
        if (active.contains(node)) throw new InvalidDependencyException("Cyclic dependsOn registration: " + node);
        if (done.contains(node)) return;
        active.add(node);
        for (String parent : graph.getOrDefault(node, List.of())) visit(parent, graph, done, active);
        active.remove(node); done.add(node);
    }
}
