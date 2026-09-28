package com.example.scheduler.history.infra.persistent;

import com.example.scheduler.execution.domain.ExecutionStatus;
import com.example.scheduler.execution.infra.persistence.JobExecutionHistoryEntity;
import com.example.scheduler.global.config.ExecutionProperties;
import com.example.scheduler.job.application.schedule.ScheduleKeyPolicy;
import jakarta.persistence.*;
import org.quartz.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.*;
import java.util.UUID;

/** One idempotent SKIPPED range per misfire notification, not one row per missed second. */
@Repository
public class MisfireSkipHistory {
    @PersistenceContext private EntityManager em;
    private final TransactionTemplate tx;
    private final ExecutionProperties node;
    public MisfireSkipHistory(PlatformTransactionManager manager, ExecutionProperties node) {
        this.node=node; tx=new TransactionTemplate(manager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); tx.setTimeout(5);
    }
    public void record(CronTrigger trigger, Instant observedAt) {
        if (trigger.getNextFireTime() == null) return;
        Instant first=trigger.getNextFireTime().toInstant();
        if (first.isAfter(observedAt)) return;
        String tenant=ScheduleKeyPolicy.extractTenantId(trigger.getJobKey().getGroup());
        String group=ScheduleKeyPolicy.extractGroup(trigger.getJobKey().getGroup());
        String name=trigger.getJobKey().getName();
        String occurrence="misfire-skip:"+first.toEpochMilli();
        String id=UUID.nameUUIDFromBytes((tenant+"\u0000"+group+"\u0000"+name+"\u0000"+occurrence)
                .getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
        try { write(trigger,observedAt,first,tenant,group,name,occurrence,id); }
        catch (RuntimeException conflict) {
            // Quartz may redeliver after rollback or on another node. Retry only an existing audit identity.
            if (!Boolean.TRUE.equals(tx.execute(status -> em.find(JobExecutionHistoryEntity.class,id)!=null))) throw conflict;
            write(trigger,observedAt,first,tenant,group,name,occurrence,id);
        }
    }
    private void write(CronTrigger trigger, Instant now, Instant first, String tenant, String group, String name, String occurrence, String id) {
        tx.executeWithoutResult(status -> {
            var local=LocalDateTime.ofInstant(now,ZoneId.systemDefault());
            String reason="Misfire SKIP; missed Cron range from="+first+", observedThrough="+now
                    +", cron="+trigger.getCronExpression()+", timezone="+trigger.getTimeZone().getID()+"; no Process/Attempt created";
            var existing=em.find(JobExecutionHistoryEntity.class,id,LockModeType.PESSIMISTIC_WRITE);
            if(existing!=null) {
                if(existing.getEndTime().isBefore(local)) em.createQuery("update JobExecutionHistoryEntity h set h.endTime=:end, h.message=:reason, h.version=h.version+1 where h.executionId=:id")
                        .setParameter("end",local).setParameter("reason",reason).setParameter("id",id).executeUpdate();
                return;
            }
            var data=trigger.getJobDataMap();
            em.persist(JobExecutionHistoryEntity.builder().executionId(id).tenantId(tenant).scheduleGroup(group).scheduleName(name)
                    .occurrenceKey(occurrence).scheduledAt(first).triggerNodeId(node.nodeId()).scheduleType("CRON")
                    .jobType(data.getString("jobType")==null ? "SHELL" : data.getString("jobType"))
                    .cronExpression(trigger.getCronExpression()).command(data.getString("command")==null ? "N/A" : data.getString("command"))
                    .parameters(data.getString("parameters")).status(ExecutionStatus.SKIPPED).startTime(local).endTime(local)
                    .duration(0L).message(reason).build());
            em.flush();
        });
    }
}
