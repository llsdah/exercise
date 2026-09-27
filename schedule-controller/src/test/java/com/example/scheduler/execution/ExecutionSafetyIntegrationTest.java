package com.example.scheduler.execution;

import com.example.scheduler.history.application.HistoryExecutionCoordinator;
import com.example.scheduler.history.domain.HistoryExecutionRequest;
import com.example.scheduler.history.domain.HistoryExecutionRepository;
import com.example.scheduler.history.domain.ExecutionStatus;
import com.example.scheduler.attempt.domain.AttemptState;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.lease.domain.StaleExecutorException;
import com.example.scheduler.history.infra.persistent.JobExecutionHistoryEntity;
import com.example.scheduler.history.infra.persistent.JobExecutionHistoryJpaRepository;
import com.example.scheduler.lease.infra.persistence.ExecutionLeaseEntity;
import com.example.scheduler.lease.infra.watchdog.ExecutionLeaseMonitor;
import com.example.scheduler.global.config.ExecutionProperties;

import com.example.scheduler.SchedulerApplication;
import com.example.scheduler.global.config.SchedulerProperties;
import com.example.scheduler.job.application.event.JobExecutionCompletedEvent;
import com.example.scheduler.job.infra.executor.JobProcessManager;
import com.example.scheduler.job.infra.executor.ShellCommandJob;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// 수정: 실제 H2 트랜잭션/행 잠금과 OS Process로 노드 충돌, 만료, stale 쓰기를 회귀 검증한다.
@SpringBootTest(classes = SchedulerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.config.location=classpath:execution-test.yml")
class ExecutionSafetyIntegrationTest {
    @Autowired HistoryExecutionCoordinator nodeA;
    @Autowired JobExecutionHistoryJpaRepository executions;
    @Autowired HistoryExecutionRepository executionStore;
    @Autowired ExecutionLeaseMonitor monitor;
    @Autowired ExecutionProperties properties;
    @Autowired JobProcessManager processes;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired AutowireCapableBeanFactory factory;
    @Autowired JdbcTemplate jdbc;
    @Autowired Scheduler scheduler;
    @Autowired com.example.scheduler.job.domain.JobRepository jobRepository;
    @Autowired com.example.scheduler.job.application.JobExecutionRecorder recorder;
    @Autowired com.example.scheduler.history.application.JobExecutionHistoryReadService historyReader;
    @PersistenceContext EntityManager em;
    HistoryExecutionCoordinator nodeB;
    @TempDir Path faultFiles;

    @BeforeEach
    void createSecondNode() {
        nodeB = new HistoryExecutionCoordinator(executionStore,
                new ExecutionProperties("node-b", Duration.ofSeconds(30), Duration.ofSeconds(5)));
        factory.autowireBean(nodeB);
    }

    @Test
    void quartzRegistersRequiredListenersAndAopCannotDuplicateProcessHistory() throws Exception {
        assertThat(scheduler.getListenerManager().getTriggerListeners()).extracting(org.quartz.TriggerListener::getName)
                .contains("JobExecutionSkipListener", "JobExecutionManagementTableCheckListener")
                .doesNotContain("JobExecutionSystemOverloadListener");
        for (int exit : List.of(0, 7)) {
            String name = UUID.randomUUID().toString();
            var context = context("echo history-once && exit " + exit, name);
            shell(nodeA, monitor, mock(ApplicationEventPublisher.class), 60L).execute(context);
            String id = executionFor(context);
            var stored = executions.findById(id).orElseThrow();
            var event = com.example.scheduler.job.application.model.JobExecution.of("test", "safety", name,
                    context.getFireInstanceId(), "0 * * * * ?", "echo history-once", null, "SHELL", "CRON", 60, stored.getStartTime());
            event.complete(stored.getStatus(), "must-not-overwrite", stored.getStartTime(), stored.getEndTime(), null);
            recorder.recordHistory(event); // Real Spring AOP, not a mocked publisher.
            assertThat(jdbc.queryForObject("select count(*) from CHECK_SCHEDULE_EXECUTION_HISTORY where SCHEDULE_NAME=?", Integer.class, name)).isEqualTo(1);
            assertThat(executions.findById(id).orElseThrow().getMessage()).contains("history-once").doesNotContain("must-not-overwrite");
            var page = historyReader.searchHistories(new com.example.scheduler.history.domain.JobExecutionHistorySearchCondition(
                    "test", "safety", name, null, stored.getStatus(), null, null), org.springframework.data.domain.PageRequest.of(0, 20));
            assertThat(page.getContent()).singleElement().satisfies(history -> assertThat(history.getExecutionId()).isEqualTo(id));
        }
    }

    @Test
    void historyIsInsertedScheduledThenUpdatedInPlaceWithAttemptAndLease() {
        String name = UUID.randomUUID().toString();
        String id = ensure(nodeA, name);
        var scheduled = executions.findById(id).orElseThrow();
        assertThat(scheduled.getStatus()).isEqualTo(ExecutionStatus.SCHEDULED);
        assertThat(scheduled.getStartTime()).isNull();
        assertThat(scheduled.getEndTime()).isNull();
        assertThat(scheduled.getAttemptCount()).isZero();
        assertThat(scheduled.getCommand()).isEqualTo("echo test");

        var claim = nodeA.claim(id).orElseThrow();
        var running = executions.findById(id).orElseThrow();
        assertThat(running.getStatus()).isEqualTo(ExecutionStatus.RUNNING);
        assertThat(running.getStartTime()).isNotNull();
        assertThat(running.getAttemptCount()).isEqualTo(1);
        assertThat(nodeA.attempts(id).getFirst().getStatus()).isEqualTo(AttemptState.STARTING);
        nodeA.started(claim, 123L, Instant.now());
        nodeA.heartbeat(claim);
        nodeA.finish(claim, 0, null, "business-output");

        var completed = executions.findById(id).orElseThrow();
        assertThat(completed.getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
        assertThat(completed.getStartTime()).isEqualTo(running.getStartTime());
        assertThat(completed.getEndTime()).isNotNull();
        assertThat(completed.getDuration()).isNotNegative();
        assertThat(completed.getMessage()).isEqualTo("business-output");
        assertThat(jdbc.queryForObject("select count(*) from CHECK_SCHEDULE_EXECUTION_HISTORY where SCHEDULE_NAME=?", Integer.class, name)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select OWNER_NODE from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID=?", String.class, id)).isNull();
        assertThat(nodeA.attempts(id).getFirst().getStatus()).isEqualTo(AttemptState.SUCCESS);
        assertThatThrownBy(() -> nodeA.finish(claim, 9, null, "overwrite" )).isInstanceOf(StaleExecutorException.class);
        assertThat(executions.findById(id).orElseThrow().getMessage()).isEqualTo("business-output");
    }

    @Test
    void failureUpdatesExistingHistoryAndKeepsProcessOutput() throws Exception {
        var context = context("echo failure-output && exit 7", UUID.randomUUID().toString());
        shell(nodeA, monitor, mock(ApplicationEventPublisher.class), 60L).execute(context);
        var history = executions.findById(executionFor(context)).orElseThrow();
        assertThat(history.getStatus()).isEqualTo(ExecutionStatus.FAILURE);
        assertThat(history.getMessage()).contains("failure-output", "Exit code: 7");
        assertThat(history.getEndTime()).isNotNull();
        assertThat(history.getDuration()).isNotNegative();
        assertThat(nodeA.attempts(history.getId()).getFirst().getStatus()).isEqualTo(AttemptState.FAILED);
    }

    @Test
    void concurrentNodesCreateOneExecutionAndOneAttempt() throws Exception {
        // 수정: 20회 경합에서 중복 논리 실행 및 중복 실행권이 없는지 검증한다.
        try (var pool = Executors.newFixedThreadPool(2)) {
            for (int round = 0; round < 20; round++) {
                String name = UUID.randomUUID().toString();
                var barrier = new CyclicBarrier(2);
                Callable<String> first = () -> { barrier.await(); return ensure(nodeA, name); };
                Callable<String> second = () -> { barrier.await(); return ensure(nodeB, name); };
                var futures = pool.invokeAll(List.of(first, second));
                String id = futures.get(0).get(10, TimeUnit.SECONDS);
                assertThat(futures.get(1).get(10, TimeUnit.SECONDS)).isEqualTo(id);
                var claims = pool.invokeAll(List.of(
                        () -> { barrier.await(); return nodeA.claim(id); },
                        () -> { barrier.await(); return nodeB.claim(id); }));
                int winners = 0;
                for (var future : claims) if (((Optional<?>) future.get(10, TimeUnit.SECONDS)).isPresent()) winners++;
                assertThat(winners).isEqualTo(1);
                assertThat(nodeA.attempts(id)).hasSize(1);
            }
        }
    }

    @Test
    void failureBeforePidRecordingIsUnknownAndOldTokenCannotWrite() {
        String id = ensure(nodeA, UUID.randomUUID().toString());
        LeaseClaim old = nodeA.claim(id).orElseThrow();
        expire(id);
        assertThat(nodeB.expireLeases()).isGreaterThanOrEqualTo(1);
        assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
        assertThat(nodeB.claim(id)).isEmpty();
        assertThat(nodeA.attempts(id)).singleElement().satisfies(attempt -> {
            assertThat(attempt.getStatus()).isEqualTo(AttemptState.UNKNOWN);
            assertThat(attempt.getPid()).isNull();
            assertThat(attempt.getEndedAt()).isNull();
        });
        assertThatThrownBy(() -> nodeA.started(old, 123L, Instant.now())).isInstanceOf(StaleExecutorException.class);
        assertThatThrownBy(() -> nodeA.heartbeat(old)).isInstanceOf(StaleExecutorException.class);
        assertThatThrownBy(() -> nodeA.finish(old, 0, null)).isInstanceOf(StaleExecutorException.class);
        assertThat(jdbc.queryForObject("select LEASE_TOKEN from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID = ?", Long.class, id))
                .isGreaterThan(old.token());
    }

    @Test
    void expirationDuringRunningRejectsLateSuccessEvenBeforeSweep() {
        String id = ensure(nodeA, UUID.randomUUID().toString());
        LeaseClaim claim = nodeA.claim(id).orElseThrow();
        nodeA.started(claim, 321L, Instant.now());
        expire(id);
        assertThatThrownBy(() -> nodeA.finish(claim, 0, null)).isInstanceOf(StaleExecutorException.class);
        nodeB.expireLeases();
        assertThat(nodeA.attempts(id).getFirst().getPid()).isEqualTo(321L);
        assertThat(nodeB.claim(id)).isEmpty();
    }

    @Test
    void heartbeatRenewsAndRejectsWrongOwnerTokenOrAttempt() {
        String id = ensure(nodeA, UUID.randomUUID().toString());
        LeaseClaim claim = nodeA.claim(id).orElseThrow();
        setExpiry(id, Instant.now().plusSeconds(3));
        nodeA.heartbeat(claim);
        assertThat(jdbc.queryForObject("select EXPIRES_AT from CHECK_BATCH_EXECUTION_LEASE where EXECUTION_ID = ?",
                java.sql.Timestamp.class, id).toInstant()).isAfter(Instant.now().plusSeconds(20));
        assertThatThrownBy(() -> nodeB.heartbeat(new LeaseClaim(id, claim.attemptId(), "node-b", claim.token())))
                .isInstanceOf(StaleExecutorException.class);
        assertThatThrownBy(() -> nodeA.heartbeat(new LeaseClaim(id, claim.attemptId(), claim.nodeId(), claim.token() - 1)))
                .isInstanceOf(StaleExecutorException.class);
        assertThatThrownBy(() -> nodeA.heartbeat(new LeaseClaim(id, id + "-99", claim.nodeId(), claim.token())))
                .isInstanceOf(StaleExecutorException.class);
        nodeA.finish(claim, 0, null);
        assertThat(nodeB.claim(id)).isEmpty();
        assertThatThrownBy(() -> nodeA.finish(claim, 9, null)).isInstanceOf(StaleExecutorException.class);
        assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    }

    @Test
    void processResultAndAttemptArePersistedAndDuplicateTriggerDoesNotRunAgain() throws Exception {
        var publisher = mock(ApplicationEventPublisher.class);
        var context = context("echo phase-one-ok", UUID.randomUUID().toString());
        var job = shell(nodeA, monitor, publisher, 60L);
        job.execute(context);
        String id = executionFor(context);
        assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
        assertThat(nodeA.attempts(id)).singleElement().satisfies(attempt -> {
            assertThat(attempt.getPid()).isPositive();
            assertThat(attempt.getExitCode()).isZero();
            assertThat(attempt.getEndedAt()).isNotNull();
            assertThat(attempt.getStatus()).isEqualTo(AttemptState.SUCCESS);
        });
        job.execute(context);
        verifyNoInteractions(publisher);
        assertThat(nodeA.attempts(id)).hasSize(1);
    }

    @Test
    void actualNonZeroExitIsRecordedAsFailed() throws Exception {
        var context = context("exit 7", UUID.randomUUID().toString());
        shell(nodeA, monitor, mock(ApplicationEventPublisher.class), 60L).execute(context);
        String id = executionFor(context);
        assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.FAILURE);
        assertThat(nodeA.attempts(id).getFirst().getExitCode()).isEqualTo(7);
    }

    @Test
    void recoveryTriggerUsesOriginalOccurrenceAndDoesNotDuplicateCompletedJob() throws Exception {
        var context = context("echo recovered-once", UUID.randomUUID().toString());
        var publisher = mock(ApplicationEventPublisher.class);
        var job = shell(nodeA, monitor, publisher, 60L);
        job.execute(context);
        String id = executionFor(context);
        when(context.isRecovering()).thenReturn(true);
        when(context.getScheduledFireTime()).thenReturn(Date.from(Instant.now()));
        context.getMergedJobDataMap().put(Scheduler.FAILED_JOB_ORIGINAL_TRIGGER_SCHEDULED_FIRETIME_IN_MILLISECONDS, "1000");
        job.execute(context);
        assertThat(nodeA.attempts(id)).hasSize(1);
        verifyNoInteractions(publisher);
    }

    @Test
    void manualRequestsAtSameTimeAreDistinctButRecoveryKeepsRequestIdentity() throws Exception {
        var context = context("echo manual-once", UUID.randomUUID().toString());
        var publisher = mock(ApplicationEventPublisher.class);
        var job = shell(nodeA, monitor, publisher, 60L);
        context.getMergedJobDataMap().put("executionRequestId", "request-one");
        job.execute(context);
        context.getMergedJobDataMap().put("executionRequestId", "request-two");
        job.execute(context);
        when(context.isRecovering()).thenReturn(true);
        context.getMergedJobDataMap().put(Scheduler.FAILED_JOB_ORIGINAL_TRIGGER_SCHEDULED_FIRETIME_IN_MILLISECONDS, "1000");
        job.execute(context);
        verifyNoInteractions(publisher);
        for (String key : List.of("manual:request-one", "manual:request-two")) {
            String id = executions.findByTenantIdAndScheduleGroupAndScheduleNameAndOccurrenceKey("test", "safety",
                    context.getJobDetail().getKey().getName(), key).orElseThrow().getId();
            assertThat(nodeA.attempts(id)).hasSize(1);
        }
    }

    @Test
    void dbFailureBeforeAcquisitionStartsNoProcess() {
        HistoryExecutionCoordinator unavailable = mock(HistoryExecutionCoordinator.class);
        when(unavailable.ensureExecution(any()))
                .thenThrow(new IllegalStateException("injected DB outage"));
        JobProcessManager isolated = mock(JobProcessManager.class);
        var job = new ShellCommandJob(isolated, unavailable, monitor,
                new SchedulerProperties(true, true, 5, 60L));
        assertThatThrownBy(() -> job.execute(context("echo should-not-run", UUID.randomUUID().toString())))
                .isInstanceOf(JobExecutionException.class);
        verifyNoInteractions(isolated);
    }

    @Test
    void dbFailureAfterStartStopsProcessAndRecoveryMarksUnknown() throws Exception {
        HistoryExecutionCoordinator failing = spy(nodeA);
        doThrow(new IllegalStateException("injected DB outage after Process.start"))
                .when(failing).started(any(), anyLong(), any());
        doThrow(new IllegalStateException("DB still unavailable")).when(failing).finish(any(), any(), any(), any());
        var publisher = mock(ApplicationEventPublisher.class);
        var context = context(waitCommand(), UUID.randomUUID().toString());
        shell(failing, monitor, publisher, 60L).execute(context);
        String id = executionFor(context);
        assertThat(processes.getRunningProcesses()).isEmpty();
        verifyNoInteractions(publisher);
        expire(id);
        nodeB.expireLeases();
        assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
        assertThat(nodeB.claim(id)).isEmpty();
    }

    @Test
    void timeoutDoesNotBlockOnSilentProcessOutput() throws Exception {
        var context = context(waitCommand(), UUID.randomUUID().toString());
        long start = System.nanoTime();
        shell(nodeA, monitor, mock(ApplicationEventPublisher.class), 1L).execute(context);
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(10));
        assertThat(executions.findById(executionFor(context)).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
        assertThat(processes.getRunningProcesses()).isEmpty();
    }

    @Test
    void backgroundHeartbeatsKeepLeaseAliveBeyondInitialTtl() throws Exception {
        var shortProperties = new ExecutionProperties("short-ttl-node", Duration.ofSeconds(2), Duration.ofMillis(200));
        var shortNode = new HistoryExecutionCoordinator(executionStore, shortProperties);
        factory.autowireBean(shortNode);
        var shortMonitor = new ExecutionLeaseMonitor(shortNode, shortProperties);
        try {
            String command = System.getProperty("os.name").toLowerCase().contains("win")
                    ? "powershell.exe -NoProfile -NonInteractive -Command \"Start-Sleep -Seconds 3\"" : "sleep 3";
            var context = context(command, UUID.randomUUID().toString());
            shell(shortNode, shortMonitor, mock(ApplicationEventPublisher.class), 10L).execute(context);
            String id = executionFor(context);
            assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
            var attempt = shortNode.attempts(id).getFirst();
            assertThat(attempt.getHeartbeatAt()).isAfter(attempt.getStartedAt().plusSeconds(2));
        } finally {
            shortMonitor.shutdown();
        }
    }

    @Test
    void lostBackgroundHeartbeatTerminatesOwnedProcessWithoutRetry() throws Exception {
        HistoryExecutionCoordinator failing = spy(nodeA);
        var count = new java.util.concurrent.atomic.AtomicInteger();
        doAnswer(invocation -> {
            if (count.incrementAndGet() > 1) throw new IllegalStateException("injected heartbeat DB outage");
            invocation.callRealMethod();
            return null;
        }).when(failing).heartbeat(any());
        var fastMonitor = new ExecutionLeaseMonitor(failing,
                new ExecutionProperties("node-a", Duration.ofSeconds(30), Duration.ofMillis(200)));
        try {
            var context = context(waitCommand(), UUID.randomUUID().toString());
            shell(nodeA, fastMonitor, mock(ApplicationEventPublisher.class), 60L).execute(context);
            String id = executionFor(context);
            assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
            assertThat(nodeB.claim(id)).isEmpty();
            assertThat(processes.getRunningProcesses()).isEmpty();
        } finally {
            fastMonitor.shutdown();
        }
    }

    @Test
    void newTablesHonorConfiguredPrefixAndQuartzKeepsItsOwnPrefix() {
        var tables = jdbc.queryForList("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'", String.class);
        assertThat(tables).contains("CHECK_SCHEDULE_EXECUTION_HISTORY", "CHECK_BATCH_ATTEMPT", "CHECK_BATCH_EXECUTION_LEASE", "QRTZ_JOB_DETAILS");
        assertThat(tables).doesNotContain("CHECK_BATCH_EXECUTION", "MY_BATCH_EXECUTION", "CHECK_QRTZ_JOB_DETAILS");
        assertThat(jdbc.queryForList("select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='CHECK_SCHEDULE_EXECUTION_HISTORY'", String.class))
                .contains("TENANT_ID", "SCHEDULE_GROUP", "SCHEDULE_NAME", "START_TIME", "FIRE_INSTANCE_ID",
                        "SCHEDULE_TYPE", "EXECUTION_COUNT", "JOB_TYPE", "JOB_ID", "CRON_EXPRESSION", "COMMAND",
                        "PARAMETERS", "STATUS", "END_TIME", "DURATION", "MESSAGE", "EXECUTION_ID", "OCCURRENCE_KEY",
                        "SCHEDULED_AT", "ATTEMPT_COUNT", "CANCELLATION_REQUESTED", "VERSION");
    }

    @Test
    void killedSchedulerJvmPreservesUnknownAtThreeCrashWindows() throws Exception {
        // 수정: 실제 JVM 강제 종료와 외부 업무 DB commit을 사용한다. Reconciliation 재실행은 Phase 2 범위다.
        Class<?> serverClass = Class.forName("org.h2.tools.Server");
        Object server = serverClass.getMethod("createTcpServer", String[].class)
                .invoke(null, (Object) new String[]{"-tcpPort", "0", "-ifNotExists"});
        serverClass.getMethod("start").invoke(server);
        int port = (Integer) serverClass.getMethod("getPort").invoke(server);
        String url = "jdbc:h2:tcp://127.0.0.1:" + port + "/mem:execution-tests";
        jdbc.execute("create table if not exists TEST_BUSINESS_MARKER (EXECUTION_ID varchar(36) primary key)");
        List<Long> detectionMillis = new ArrayList<>();
        try {
            for (String mode : List.of("CLAIMED", "STARTED", "COMMITTED")) {
                Path ready = faultFiles.resolve(mode + ".ready");
                Path log = faultFiles.resolve(mode + ".log");
                String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
                Process node = new ProcessBuilder(java, "-cp", System.getProperty("test.runtime.classpath"),
                        FaultNodeProcess.class.getName(), mode, url, ready.toString())
                        .redirectErrorStream(true).redirectOutput(log.toFile()).start();
                long batchPid = -1;
                try {
                    // 수정: 느린 CI의 Spring/JPA 기동 시간은 장애 감지 시간 측정에서 분리한다.
                    long readyDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(90);
                    while (!Files.exists(ready) && node.isAlive() && System.nanoTime() < readyDeadline) Thread.sleep(100);
                    assertThat(Files.exists(ready)).withFailMessage(() -> {
                        try { return Files.readString(log); } catch (Exception ignored) { return "Missing fault node ready signal"; }
                    }).isTrue();
                    String[] fields = Files.readString(ready).split(",");
                    String id = fields[0];
                    LeaseClaim stale = new LeaseClaim(id, fields[1], "fault-node", Long.parseLong(fields[2]));
                    batchPid = Long.parseLong(fields[3]);
                    node.destroyForcibly();
                    assertThat(node.waitFor(10, TimeUnit.SECONDS)).isTrue();
                    long killedAt = System.nanoTime();
                    while (executions.findById(id).orElseThrow().getStatus() == ExecutionStatus.RUNNING
                            && System.nanoTime() - killedAt < TimeUnit.SECONDS.toNanos(10)) {
                        nodeB.expireLeases();
                        Thread.sleep(100);
                    }
                    detectionMillis.add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - killedAt));
                    assertThat(executions.findById(id).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
                    assertThat(nodeB.claim(id)).isEmpty();
                    assertThat(nodeB.attempts(id)).hasSize(1);
                    assertThatThrownBy(() -> nodeA.finish(stale, 0, null)).isInstanceOf(StaleExecutorException.class);
                    int markerCount = jdbc.queryForObject("select count(*) from TEST_BUSINESS_MARKER where EXECUTION_ID = ?", Integer.class, id);
                    assertThat(markerCount).isEqualTo(mode.equals("COMMITTED") ? 1 : 0);
                    if (mode.equals("STARTED")) assertThat(nodeB.attempts(id).getFirst().getPid()).isNull();
                } finally {
                    // 수정: 준비 신호 이전 테스트 실패 시에도 생성한 자식 JVM을 남기지 않는다.
                    if (node.isAlive()) node.descendants().forEach(ProcessHandle::destroyForcibly);
                    if (node.isAlive()) { node.destroyForcibly(); node.waitFor(10, TimeUnit.SECONDS); }
                    if (batchPid > 0) ProcessHandle.of(batchPid).filter(ProcessHandle::isAlive).ifPresent(ProcessHandle::destroyForcibly);
                }
            }
            System.out.printf("FAULT_JVM_RESULT crashes=3 missingExecutions=0 duplicateBusinessCommits=0 averageDetectionMs=%.1f maxDetectionMs=%d%n",
                    detectionMillis.stream().mapToLong(Long::longValue).average().orElseThrow(), Collections.max(detectionMillis));
        } finally {
            serverClass.getMethod("stop").invoke(server);
        }
    }

    @Test
    void quartzCreatesInjectedJobAndRunsThroughLeasePath() throws Exception {
        String name = UUID.randomUUID().toString();
        JobDetail detail = JobBuilder.newJob(ShellCommandJob.class).withIdentity(name, "test::safety")
                .usingJobData("command", "echo quartz-lease-ok").usingJobData("jobType", "SHELL")
                .usingJobData("scheduleType", "MANUAL").usingJobData("cronExpression", "0 * * * * ?")
                .storeDurably().requestRecovery(true).build();
        try {
            saveMeta(detail);
            scheduler.addJob(detail, true);
            JobDataMap request = new JobDataMap();
            request.put("executionRequestId", "quartz-test");
            scheduler.triggerJob(detail.getKey(), request);
            scheduler.start();
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
            JobExecutionHistoryEntity result = null;
            while (System.nanoTime() < deadline) {
                result = executions.findByTenantIdAndScheduleGroupAndScheduleNameAndOccurrenceKey("test", "safety", name, "manual:quartz-test").orElse(null);
                if (result != null && result.getStatus() == ExecutionStatus.SUCCESS) break;
                Thread.sleep(100);
            }
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
        } finally {
            scheduler.standby();
            scheduler.deleteJob(detail.getKey());
        }
    }

    @Test
    void twoLiveQuartzJvmsShareTriggersAndRouteCancellationToOwner() throws Exception {
        Class<?> serverClass = Class.forName("org.h2.tools.Server");
        Object server = serverClass.getMethod("createTcpServer", String[].class)
                .invoke(null, (Object) new String[]{"-tcpPort", "0", "-ifNotExists"});
        serverClass.getMethod("start").invoke(server);
        String url = "jdbc:h2:tcp://127.0.0.1:" + serverClass.getMethod("getPort").invoke(server) + "/mem:execution-tests";
        List<Process> nodes = new ArrayList<>();
        List<JobKey> jobs = new ArrayList<>();
        try {
            for (String nodeId : List.of("cluster-a", "cluster-b")) {
                Path log = faultFiles.resolve(nodeId + ".log");
                nodes.add(new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                        "-cp", System.getProperty("test.runtime.classpath"), ClusterNodeProcess.class.getName(),
                        url, nodeId, faultFiles.toString()).redirectErrorStream(true).redirectOutput(log.toFile()).start());
            }
            for (String nodeId : List.of("cluster-a", "cluster-b")) {
                awaitCluster(() -> Files.exists(faultFiles.resolve(nodeId + ".ready")), 90);
            }
            assertThat(Files.readString(faultFiles.resolve("cluster-a.ready")))
                    .isNotEqualTo(Files.readString(faultFiles.resolve("cluster-b.ready")));
            assertThat(jdbc.queryForObject("select count(*) from QRTZ_SCHEDULER_STATE where SCHED_NAME = ?",
                    Integer.class, scheduler.getSchedulerName())).isGreaterThanOrEqualTo(2);

            String contestedName = UUID.randomUUID().toString();
            for (String node : List.of("cluster-a", "cluster-b")) Files.writeString(faultFiles.resolve(node + ".race"), contestedName);
            awaitCluster(() -> Files.exists(faultFiles.resolve("cluster-a.race-ready"))
                    && Files.exists(faultFiles.resolve("cluster-b.race-ready")), 10);
            Files.writeString(faultFiles.resolve("race-go"), "go");
            awaitCluster(() -> Files.exists(faultFiles.resolve("cluster-a.race-result"))
                    && Files.exists(faultFiles.resolve("cluster-b.race-result")), 15);
            String[] raceA = Files.readString(faultFiles.resolve("cluster-a.race-result")).split(",");
            String[] raceB = Files.readString(faultFiles.resolve("cluster-b.race-result")).split(",");
            assertThat(raceA[0]).isEqualTo(raceB[0]);
            assertThat(List.of(raceA[1], raceB[1])).containsExactlyInAnyOrder("true", "false");
            assertThat(nodeA.attempts(raceA[0])).hasSize(1);
            assertThat(jdbc.queryForObject("select count(*) from CHECK_SCHEDULE_EXECUTION_HISTORY where SCHEDULE_NAME=?", Integer.class, contestedName)).isEqualTo(1);

            for (int i = 0; i < 12; i++) {
                JobDetail detail = clusterJob("echo cluster-once");
                jobs.add(detail.getKey());
                scheduler.scheduleJob(detail, TriggerBuilder.newTrigger().forJob(detail).startNow().build());
            }
            awaitCluster(() -> jdbc.queryForObject("select count(*) from CHECK_SCHEDULE_EXECUTION_HISTORY "
                    + "where SCHEDULE_GROUP='cluster' and STATUS='SUCCESS'", Integer.class) == 12, 60);
            assertThat(jdbc.queryForObject("select count(*) from CHECK_BATCH_ATTEMPT a join CHECK_SCHEDULE_EXECUTION_HISTORY e "
                    + "on a.EXECUTION_ID=e.EXECUTION_ID where e.SCHEDULE_GROUP='cluster'", Integer.class)).isEqualTo(12);
            for (JobKey key : jobs) {
                try (var deliveries = Files.list(faultFiles)) {
                    assertThat(deliveries.filter(path -> path.getFileName().toString().startsWith(key.getName() + ".fired-")).count()).isEqualTo(1);
                }
                assertThat(jdbc.queryForObject("select count(*) from CHECK_SCHEDULE_EXECUTION_HISTORY where SCHEDULE_NAME=?", Integer.class, key.getName())).isEqualTo(1);
            }

            JobDetail waiting = clusterJob(waitCommand());
            jobs.add(waiting.getKey());
            scheduler.scheduleJob(waiting, TriggerBuilder.newTrigger().forJob(waiting).startNow().build());
            awaitCluster(() -> !jdbc.queryForList("select a.NODE_ID from CHECK_BATCH_ATTEMPT a join CHECK_SCHEDULE_EXECUTION_HISTORY e "
                    + "on a.EXECUTION_ID=e.EXECUTION_ID where e.SCHEDULE_NAME=? and a.PID is not null",
                    String.class, waiting.getKey().getName()).isEmpty(), 20);
            var execution = executions.findAll().stream().filter(e -> e.getScheduleName().equals(waiting.getKey().getName()))
                    .findFirst().orElseThrow();
            var attempt = nodeA.attempts(execution.getId()).getFirst();
            String otherNode = attempt.getNodeId().equals("cluster-a") ? "cluster-b" : "cluster-a";
            // Send the API service call to the JVM that owns no process for this execution.
            Files.writeString(faultFiles.resolve(otherNode + ".cancel"), waiting.getKey().getName());
            awaitCluster(() -> Files.exists(faultFiles.resolve(otherNode + ".accepted")), 10);
            awaitCluster(() -> executions.findById(execution.getId()).orElseThrow().getStatus() == ExecutionStatus.UNKNOWN, 15);
            assertThat(ProcessHandle.of(attempt.getPid()).map(ProcessHandle::isAlive).orElse(false)).isFalse();
            assertThat(nodeA.attempts(execution.getId())).hasSize(1);
            JobDetail crashing = clusterJob(waitCommand());
            jobs.add(crashing.getKey());
            scheduler.scheduleJob(crashing, TriggerBuilder.newTrigger().forJob(crashing).startNow().build());
            awaitCluster(() -> !jdbc.queryForList("select a.NODE_ID from CHECK_BATCH_ATTEMPT a join CHECK_SCHEDULE_EXECUTION_HISTORY e "
                    + "on a.EXECUTION_ID=e.EXECUTION_ID where e.SCHEDULE_NAME=? and a.PID is not null",
                    String.class, crashing.getKey().getName()).isEmpty(), 20);
            var crashedExecution = executions.findAll().stream().filter(e -> e.getScheduleName().equals(crashing.getKey().getName()))
                    .findFirst().orElseThrow();
            var crashedAttempt = nodeA.attempts(crashedExecution.getId()).getFirst();
            Process owner = nodes.get(crashedAttempt.getNodeId().equals("cluster-a") ? 0 : 1);
            var descendants = owner.descendants().toList();
            try {
                owner.destroyForcibly();
                assertThat(owner.waitFor(10, TimeUnit.SECONDS)).isTrue();
                awaitCluster(() -> Files.exists(faultFiles.resolve(crashing.getKey().getName() + ".recovered")), 60);
                assertThat(Files.readString(faultFiles.resolve(crashing.getKey().getName() + ".recovered"))).isEqualTo("ok");
                awaitCluster(() -> executions.findById(crashedExecution.getId()).orElseThrow().getStatus() == ExecutionStatus.UNKNOWN, 15);
                assertThat(nodeA.attempts(crashedExecution.getId())).hasSize(1);
                assertThat(executions.findAll().stream().filter(e -> e.getScheduleName().equals(crashing.getKey().getName())).count())
                        .isEqualTo(1);
                var stale = new LeaseClaim(crashedExecution.getId(), crashedAttempt.getId(), crashedAttempt.getNodeId(), crashedAttempt.getLeaseToken());
                assertThatThrownBy(() -> nodeA.heartbeat(stale)).isInstanceOf(StaleExecutorException.class);
                assertThatThrownBy(() -> nodeA.finish(stale, 0, null, "late success")).isInstanceOf(StaleExecutorException.class);
                assertThat(nodeA.claim(crashedExecution.getId())).isEmpty();
                assertThat(executions.findById(crashedExecution.getId()).orElseThrow().getStatus()).isEqualTo(ExecutionStatus.UNKNOWN);
            } finally {
                descendants.stream().filter(ProcessHandle::isAlive).forEach(ProcessHandle::destroyForcibly);
            }
            System.out.println("QUARTZ_CLUSTER_RESULT activeJvms=2 completed=12 triggerDeliveries=12 logicalRace=passed duplicateAttempts=0 remoteCancellation=passed recovery=passed staleFencing=passed noUnknownRetry=passed");
        } finally {
            Files.writeString(faultFiles.resolve("stop"), "stop");
            for (Process node : nodes) {
                if (!node.waitFor(10, TimeUnit.SECONDS)) {
                    node.descendants().forEach(ProcessHandle::destroyForcibly);
                    node.destroyForcibly();
                    node.waitFor(10, TimeUnit.SECONDS);
                }
            }
            for (JobKey key : jobs) scheduler.deleteJob(key);
            serverClass.getMethod("stop").invoke(server);
        }
    }

    private JobDetail clusterJob(String command) {
        var detail = JobBuilder.newJob(ShellCommandJob.class).withIdentity(UUID.randomUUID().toString(), "test::cluster")
                .usingJobData("command", command).usingJobData("jobType", "SHELL")
                .usingJobData("cronExpression", "0 * * * * ?")
                .usingJobData("scheduleType", "MANUAL").storeDurably().requestRecovery(true).build();
        saveMeta(detail);
        return detail;
    }

    private void saveMeta(JobDetail detail) {
        jobRepository.save(com.example.scheduler.job.domain.Job.create("test", detail.getKey().getGroup().split("::")[1],
                detail.getKey().getName(), "MANUAL", "SHELL", "0 * * * * ?", detail.getJobDataMap().getString("command"),
                null, "cluster test", null, null, "test"));
    }

    private void awaitCluster(java.util.concurrent.Callable<Boolean> condition, int seconds) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
        while (System.nanoTime() < deadline) {
            if (condition.call()) return;
            Thread.sleep(100);
        }
        StringBuilder logs = new StringBuilder();
        for (String node : List.of("cluster-a", "cluster-b")) {
            Path log = faultFiles.resolve(node + ".log");
            if (Files.exists(log)) logs.append(Files.readString(log));
        }
        fail("Cluster condition timed out: " + logs);
    }

    String ensure(HistoryExecutionCoordinator node, String name) {
        return node.ensureExecution(new HistoryExecutionRequest("test", "safety", name, "scheduled:1000", Instant.ofEpochMilli(1000), null, "CRON", "SHELL", "0 * * * * ?", "echo test", null));
    }

    void expire(String id) { setExpiry(id, Instant.EPOCH); }

    void setExpiry(String id, Instant expiry) {
        new TransactionTemplate(transactionManager).executeWithoutResult(tx -> {
            var lease = em.find(ExecutionLeaseEntity.class, id);
            org.springframework.test.util.ReflectionTestUtils.setField(lease, "expiresAt", expiry);
        });
    }

    ShellCommandJob shell(HistoryExecutionCoordinator coordinator, ExecutionLeaseMonitor leaseMonitor,
                          ApplicationEventPublisher publisher, long timeout) {
        return new ShellCommandJob(processes, coordinator, leaseMonitor, new SchedulerProperties(true, true, 5, timeout));
    }

    JobExecutionContext context(String command, String name) {
        var context = mock(JobExecutionContext.class);
        var detail = JobBuilder.newJob(ShellCommandJob.class).withIdentity(name, "test::safety")
                .usingJobData("command", command).usingJobData("jobType", "SHELL").usingJobData("scheduleType", "CRON")
                .usingJobData("cronExpression", "0 * * * * ?").build();
        when(context.getJobDetail()).thenReturn(detail);
        when(context.getMergedJobDataMap()).thenReturn(detail.getJobDataMap());
        when(context.getScheduledFireTime()).thenReturn(Date.from(Instant.ofEpochMilli(1000)));
        when(context.getFireInstanceId()).thenReturn(UUID.randomUUID().toString());
        return context;
    }

    String executionFor(JobExecutionContext context) {
        return executions.findByTenantIdAndScheduleGroupAndScheduleNameAndOccurrenceKey("test", "safety",
                context.getJobDetail().getKey().getName(), "scheduled:1000").orElseThrow().getId();
    }

    String waitCommand() {
        return System.getProperty("os.name").toLowerCase().contains("win")
                ? "powershell.exe -NoProfile -NonInteractive -Command \"Start-Sleep -Seconds 20\"" : "sleep 20";
    }
}
