package com.example.scheduler.dependency.infra.persistence;

import com.example.scheduler.SchedulerApplication;
import com.example.scheduler.dependency.domain.BusinessOccurrence;
import com.example.scheduler.dependency.domain.InvalidDependencyException;
import com.example.scheduler.execution.domain.ExecutionStatus;
import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.execution.infra.persistence.JobExecutionHistoryEntity;
import com.example.scheduler.job.domain.Job;
import com.example.scheduler.job.infra.persistence.JobEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = SchedulerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.config.location=classpath:execution-test.yml")
@Transactional
class DependencySemanticsTest {
    @Autowired EntityManager em;
    @Autowired DatabaseExecutionDependencies dependencies;
    @Autowired DependencyRegistrationGuard guard;

    @Test void eachDailyCycleRequiresItsOwnPredecessorSuccess() {
        job("parent", "CRON", List.of());
        job("child", "CRON", List.of("parent"));
        var first = execution("parent", 0, "scheduled", ExecutionStatus.SUCCESS);
        var secondChild = execution("child", 6, "scheduled", ExecutionStatus.SCHEDULED);
        assertThat(dependencies.evaluate(first).ready()).isTrue();
        assertThat(dependencies.evaluate(execution("child", 0, "scheduled", ExecutionStatus.SCHEDULED)).ready()).isTrue();
        assertThat(dependencies.evaluate(secondChild).ready()).isFalse();
        var second = execution("parent", 6, "scheduled", ExecutionStatus.RUNNING);
        assertThat(dependencies.evaluate(secondChild).ready()).isFalse();
        em.find(JobExecutionHistoryEntity.class, second.getId()).setStatus(ExecutionStatus.SUCCESS);
        assertThat(dependencies.evaluate(secondChild).ready()).isTrue();
    }

    @Test void manualRunsBypassDependenciesAndCannotSatisfyScheduledRuns() {
        job("child", "CRON", List.of("parent"));
        var manual = execution("child", 0, "manual", ExecutionStatus.SCHEDULED);
        assertThat(dependencies.evaluate(manual).ready()).isTrue();
        execution("parent", 0, "manual", ExecutionStatus.SUCCESS);
        assertThat(dependencies.evaluate(execution("child", 0, "scheduled", ExecutionStatus.SCHEDULED)).ready()).isFalse();
        execution("parent", 0, "scheduled", ExecutionStatus.SUCCESS);
        var second = execution("child", 6, "scheduled", ExecutionStatus.SCHEDULED);
        assertThat(dependencies.evaluate(second).ready()).isFalse();
        execution("parent", 6, "scheduled", ExecutionStatus.SUCCESS);
        assertThat(dependencies.evaluate(second).ready()).isTrue();
    }

    @Test void cyclesAndNonCronParticipantsAreRejectedIncludingForwardReferences() {
        job("a", "CRON", List.of("b"));
        guard.lock();
        assertThatThrownBy(() -> guard.validate("test", "dependencies", "b", "CRON", List.of("a")))
                .isInstanceOf(InvalidDependencyException.class).hasMessageContaining("Cyclic");
        assertThatThrownBy(() -> guard.validate("test", "dependencies", "b", "MANUAL", List.of()))
                .isInstanceOf(InvalidDependencyException.class).hasMessageContaining("Predecessor");
        assertThatThrownBy(() -> guard.validate("test", "dependencies", "c", "MANUAL", List.of("a")))
                .isInstanceOf(InvalidDependencyException.class);
        assertThatThrownBy(() -> guard.validate("test", "dependencies", "c", "CRON", List.of("c")))
                .isInstanceOf(InvalidDependencyException.class).hasMessageContaining("Cyclic");
        guard.validate("test", "dependencies", "b", "CRON", List.of());
    }

    @Test void cycleUsesScheduledTimeAndBusinessDate() {
        var early = logical("parent", 0, "scheduled", ExecutionStatus.SUCCESS);
        var later = logical("child", 0, "scheduled", ExecutionStatus.SUCCESS);
        var next = logical("parent", 6, "scheduled", ExecutionStatus.SUCCESS);
        var zone = ZoneId.of("Asia/Seoul");
        assertThat(BusinessOccurrence.resolve(early, zone).key()).isEqualTo(BusinessOccurrence.resolve(later, zone).key());
        assertThat(BusinessOccurrence.resolve(next, zone).key()).isNotEqualTo(BusinessOccurrence.resolve(early, zone).key());
    }

    private void job(String name, String type, List<String> parents) {
        em.persist(JobEntity.from(Job.create("test", "dependencies", name, type, "SHELL",
                "0 0 0,6 * * ?", "echo test", null, null, null, null, "test")
                .withSchedulingSemantics(parents, null)));
        em.flush();
    }

    private LogicalExecution execution(String name, int hour, String trigger, ExecutionStatus state) {
        var e = logical(name, hour, trigger, state);
        em.persist(JobExecutionHistoryEntity.from(e));
        dependencies.capture(e);
        em.flush();
        return e;
    }

    private LogicalExecution logical(String name, int hour, String trigger, ExecutionStatus state) {
        int minute = name.equals("child") ? 10 : 0;
        var time = LocalDateTime.of(2026, 9, 28, hour, minute).atZone(ZoneId.systemDefault()).toInstant();
        return LogicalExecution.builder().executionId(UUID.randomUUID().toString()).tenantId("test")
                .scheduleGroup("dependencies").scheduleName(name).scheduleType("CRON").jobType("SHELL")
                .cronExpression("0 " + minute + " 0,6 * * ?").command("echo test").executionCount(0L)
                .scheduledAt(time).occurrenceKey(trigger + ":" + time.toEpochMilli()).status(state).build();
    }
}
