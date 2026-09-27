package com.example.scheduler.execution;

import com.example.scheduler.SchedulerApplication;
import com.example.scheduler.job.application.JobService;
import org.quartz.Scheduler;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import java.nio.file.*;

/** Independent, live Quartz JVM controlled by the integration test. */
public class ClusterNodeProcess {
    public static void main(String[] args) throws Exception {
        Path directory = Path.of(args[2]);
        try (var context = new SpringApplicationBuilder(SchedulerApplication.class)
                .web(WebApplicationType.NONE).run(
                        "--spring.config.location=classpath:execution-test.yml",
                        "--spring.datasource.url=" + args[0],
                        "--spring.jpa.hibernate.ddl-auto=validate",
                        "--spring.quartz.jdbc.initialize-schema=never",
                        "--spring.quartz.auto-startup=true",
                        "--app.execution.node-id=" + args[1],
                        "--app.execution.lease-ttl=5s",
                        "--app.execution.heartbeat-interval=500ms",
                        "--app.execution.sweep-interval-ms=500")) {
            Scheduler scheduler = context.getBean(Scheduler.class);
            scheduler.getListenerManager().addJobListener(new org.quartz.listeners.JobListenerSupport() {
                @Override public String getName() { return "cluster-recovery-observer"; }
                @Override public void jobToBeExecuted(org.quartz.JobExecutionContext job) {
                    try {
                        Files.writeString(directory.resolve(job.getJobDetail().getKey().getName() + ".fired-" + args[1] + "-" + job.getFireInstanceId()), "fired");
                    } catch (java.io.IOException failure) { throw new java.io.UncheckedIOException(failure); }
                }
                @Override public void jobWasExecuted(org.quartz.JobExecutionContext job, org.quartz.JobExecutionException error) {
                    if (job.isRecovering()) {
                        try {
                            Files.writeString(directory.resolve(job.getJobDetail().getKey().getName() + ".recovered"),
                                    error == null ? "ok" : error.toString());
                        } catch (java.io.IOException failure) { throw new java.io.UncheckedIOException(failure); }
                    }
                }
            });
            Files.writeString(directory.resolve(args[1] + ".ready"), scheduler.getSchedulerInstanceId());
            while (!Files.exists(directory.resolve("stop"))) {
                Path race = directory.resolve(args[1] + ".race");
                if (Files.exists(race)) {
                    Files.writeString(directory.resolve(args[1] + ".race-ready"), "ready");
                    if (Files.exists(directory.resolve("race-go"))) {
                        var coordinator = context.getBean(com.example.scheduler.history.application.HistoryExecutionCoordinator.class);
                        String id = coordinator.ensureExecution(new com.example.scheduler.history.domain.HistoryExecutionRequest(
                                "test", "race", Files.readString(race), "scheduled:1000", java.time.Instant.ofEpochMilli(1000),
                                null, "CRON", "SHELL", "0 * * * * ?", "echo race", null));
                        boolean won = coordinator.claim(id).isPresent();
                        Files.delete(race);
                        Files.writeString(directory.resolve(args[1] + ".race-result"), id + "," + won);
                    }
                }
                Path cancel = directory.resolve(args[1] + ".cancel");
                if (Files.exists(cancel)) {
                    context.getBean(JobService.class).killJob("test", "cluster", Files.readString(cancel));
                    Files.delete(cancel);
                    Files.writeString(directory.resolve(args[1] + ".accepted"), "ok");
                }
                Thread.sleep(100);
            }
        }
    }
}
