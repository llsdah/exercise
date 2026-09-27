package com.example.scheduler.job.infra.listener;

import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.job.domain.JobRepository;
import com.example.scheduler.job.application.event.OrphanScheduleDetectedEvent;
import com.example.scheduler.job.application.event.JobExecutionCompletedEvent;
import com.example.scheduler.job.infra.executor.ShellCommandJob;
import com.example.scheduler.job.infra.persistence.JobSkipJpaRepository;
import com.example.scheduler.job.infra.persistence.JobSkipEntity;
import com.example.scheduler.system.job.HangCheckJob;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.springframework.context.ApplicationEventPublisher;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class QuartzExecutionListenersTest {
    private final JobRepository jobs = mock(JobRepository.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);

    @Test
    void metaChecksRespectFlagsAndNeverInspectSystemJobsOrRecovery() {
        var context = context(ShellCommandJob.class);
        assertThat(meta(false, true).vetoJobExecution(context.getTrigger(), context)).isFalse();
        assertThat(meta(true, false).vetoJobExecution(context.getTrigger(), context)).isFalse();
        when(context.isRecovering()).thenReturn(true);
        assertThat(meta(true, true).vetoJobExecution(context.getTrigger(), context)).isFalse();
        var system = context(HangCheckJob.class);
        assertThat(meta(true, true).vetoJobExecution(system.getTrigger(), system)).isFalse();
        verifyNoInteractions(jobs, events);
    }

    @Test
    void missingAndDisabledMetadataVetoAndDatabaseFailureDoesNotRunUnvalidatedProcess() {
        var context = context(ShellCommandJob.class);
        when(jobs.findJob("tenant", "group", "name")).thenReturn(Optional.empty());
        assertThat(meta(true, true).vetoJobExecution(context.getTrigger(), context)).isTrue();
        verify(events).publishEvent(any(OrphanScheduleDetectedEvent.class));
        var job = mock(com.example.scheduler.job.domain.Job.class);
        when(jobs.findJob("tenant", "group", "name")).thenReturn(Optional.of(job));
        when(job.isUseYn()).thenReturn(false);
        assertThat(meta(true, true).vetoJobExecution(context.getTrigger(), context)).isTrue();
        when(job.isUseYn()).thenReturn(true);
        assertThat(meta(true, true).vetoJobExecution(context.getTrigger(), context)).isFalse();
        when(jobs.findJob("tenant", "group", "name")).thenThrow(new IllegalStateException("DB unavailable"));
        assertThat(meta(true, true).vetoJobExecution(context.getTrigger(), context)).isTrue();
    }

    @Test
    void skipVetoRetainsJobSnapshotButDoesNotConsumeRecoveryOrSystemReservations() {
        var reservations = mock(JobSkipJpaRepository.class);
        var listener = new JobExecutionSkipListener(reservations, events);
        var context = context(ShellCommandJob.class);
        var reservation = mock(JobSkipEntity.class);
        when(reservation.getTenantId()).thenReturn("tenant");
        when(reservation.getScheduleGroup()).thenReturn("group");
        when(reservation.getScheduleName()).thenReturn("name");
        when(reservation.getSkipTime()).thenReturn(LocalDateTime.of(2026, 9, 26, 1, 0));
        when(reservations.findById(any())).thenReturn(Optional.of(reservation));
        assertThat(listener.vetoJobExecution(context.getTrigger(), context)).isTrue();
        var event = org.mockito.ArgumentCaptor.forClass(JobExecutionCompletedEvent.class);
        verify(events).publishEvent(event.capture());
        assertThat(event.getValue().execution().getCommand()).isEqualTo("echo job-command");
        assertThat(event.getValue().execution().getFireInstanceId()).isEqualTo("fire-1");
        verify(reservations).delete(reservation);
        clearInvocations(reservations, events);
        when(context.isRecovering()).thenReturn(true);
        assertThat(listener.vetoJobExecution(context.getTrigger(), context)).isFalse();
        var system = context(HangCheckJob.class);
        assertThat(listener.vetoJobExecution(system.getTrigger(), system)).isFalse();
        verifyNoInteractions(reservations, events);
    }

    private JobManageTableValidationListener meta(boolean use, boolean check) {
        return new JobManageTableValidationListener(jobs, new SchedulerProperties(use, check, 5, 60L), events);
    }

    private JobExecutionContext context(Class<? extends Job> jobClass) {
        var detail = JobBuilder.newJob(jobClass).withIdentity("name", "tenant::group")
                .usingJobData("command", "echo job-command").build();
        var context = mock(JobExecutionContext.class);
        var trigger = TriggerBuilder.newTrigger().forJob(detail).build();
        when(context.getJobDetail()).thenReturn(detail);
        when(context.getTrigger()).thenReturn(trigger);
        when(context.getFireInstanceId()).thenReturn("fire-1");
        when(context.getScheduledFireTime()).thenReturn(Date.from(LocalDateTime.of(2026, 9, 26, 1, 0).atZone(ZoneId.systemDefault()).toInstant()));
        return context;
    }
}
