package com.example.scheduler.dependency.infra.persistence;

import com.example.scheduler.dependency.domain.*;
import com.example.scheduler.execution.domain.*;
import com.example.scheduler.execution.infra.persistence.JobExecutionHistoryEntity;
import com.example.scheduler.job.infra.persistence.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class DatabaseExecutionDependencies implements ExecutionDependencies {
    @PersistenceContext private EntityManager em;
    private final java.time.ZoneId businessZone;
    public DatabaseExecutionDependencies(@org.springframework.beans.factory.annotation.Value(
            "${app.dependency.business-time-zone:Asia/Seoul}") String businessTimeZone) {
        businessZone = java.time.ZoneId.of(businessTimeZone);
    }
    private List<String> configured(LogicalExecution e) {
        var job = em.find(JobEntity.class, new JobEntityId(e.getTenantId(),e.getScheduleGroup(),e.getScheduleName()));
        return job == null ? List.of() : DependencyNames.decode(job.getDependsOn());
    }
    public void capture(LogicalExecution e) {
        // Quartz can fire before registration's metadata transaction commits. Wait for the graph
        // writer before snapshotting, so an uncommitted new Job cannot appear dependency-free.
        if (em.find(DependencyGraphLock.class, "GLOBAL", LockModeType.PESSIMISTIC_READ) == null)
            throw new IllegalStateException("Dependency graph lock is missing");
        em.persist(new ExecutionDependencySnapshot(e.getId(), DependencyNames.encode(configured(e)),
                BusinessOccurrence.resolve(e, businessZone)));
    }
    public Decision evaluate(LogicalExecution e) {
        if (e.getOccurrenceKey() != null && e.getOccurrenceKey().startsWith("manual:"))
            return new Decision(true,false,null);
        var snapshot = em.find(ExecutionDependencySnapshot.class, e.getId());
        if (snapshot == null) return configured(e).isEmpty() ? new Decision(true,false,null)
                : new Decision(false,true,"Dependency snapshot missing for legacy execution");
        var parents = DependencyNames.decode(snapshot.names());
        if (parents.isEmpty()) return new Decision(true,false,null);
        String key = snapshot.businessOccurrenceKey();
        if (key == null) return new Decision(false,true,"Business occurrence unresolved: "
                + (snapshot.occurrenceReason() == null ? "legacy snapshot requires operator review" : snapshot.occurrenceReason()));
        var start = BusinessOccurrence.start(key);
        var end = BusinessOccurrence.end(key);
        List<String> waiting = new ArrayList<>();
        for (String parent : parents) {
            var candidates = em.createQuery("select h from JobExecutionHistoryEntity h, ExecutionDependencySnapshot s "
                    + "where s.executionId=h.executionId and s.businessOccurrenceKey=:key and h.tenantId=:tenant and h.scheduleGroup=:group "
                    + "and h.scheduleName=:name and h.scheduledAt>=:start and h.scheduledAt<:end "
                    + "and h.occurrenceKey like 'scheduled:%'",
                    JobExecutionHistoryEntity.class)
                    .setParameter("tenant",e.getTenantId())
                    .setParameter("group",e.getScheduleGroup())
                    .setParameter("name",parent)
                    .setParameter("start",start)
                    .setParameter("end",end)
                    .setParameter("key",key)
                    .setMaxResults(2)
                    .getResultList();
            if (candidates.size() > 1) return new Decision(false,true,"Ambiguous predecessor " + parent + " at " + key);
            if (candidates.isEmpty()) {
                var skipped = em.createQuery("select count(h) from JobExecutionHistoryEntity h " +
                                "where h.tenantId=:tenant and h.scheduleGroup=:group and h.scheduleName=:name and h.status=:status and h.occurrenceKey like 'misfire-skip:%' and h.scheduledAt<:end and h.endTime>=:local", Long.class)
                        .setParameter("tenant",e.getTenantId())
                        .setParameter("group",e.getScheduleGroup())
                        .setParameter("name",parent)
                        .setParameter("status",ExecutionStatus.SKIPPED)
                        .setParameter("end",end)
                        .setParameter("local", LocalDateTime.ofInstant(start,java.time.ZoneId.systemDefault()))
                        .getSingleResult();
                if (skipped > 0) return new Decision(false,true,"Predecessor " + parent + " occurrence was skipped by misfire policy");
                waiting.add(parent + "=MISSING"); continue;
            }
            var predecessor = candidates.getFirst();
            var parentSnapshot = em.find(ExecutionDependencySnapshot.class, predecessor.getId());
            if (parentSnapshot == null || !key.equals(parentSnapshot.businessOccurrenceKey()))
                return new Decision(false,true,"Predecessor " + parent + " has an unresolved/different business occurrence at " + key);
            var state = predecessor.getStatus();
            if (state == ExecutionStatus.SUCCESS) continue;
            if (state == ExecutionStatus.RUNNING || state == ExecutionStatus.WAITING || state == ExecutionStatus.RETRY_WAIT || state == ExecutionStatus.SCHEDULED)
                waiting.add(parent + "=" + state);
            else return new Decision(false,true,"Predecessor " + parent + " at " + key + " is " + state);
        }
        return waiting.isEmpty() ? new Decision(true,false,null)
                : new Decision(false,false,"Dependency wait at " + key + ": " + String.join(", ",waiting));
    }
}
