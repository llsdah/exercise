package com.example.scheduler.execution;

// 수정: 기존 장애 테스트가 재배치된 실행 서비스/도메인을 참조하도록 한다.
import com.example.scheduler.history.application.HistoryExecutionCoordinator;
import com.example.scheduler.history.domain.HistoryExecutionRequest;
import com.example.scheduler.lease.domain.LeaseClaim;

import com.example.scheduler.SchedulerApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import java.nio.file.*;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

// 수정: 테스트 전용 별도 Scheduler/업무 JVM을 띄워 실제 강제 종료 지점을 재현한다.
public class FaultNodeProcess {
    public static void main(String[] args) throws Exception {
        if (args[0].equals("batch")) {
            if (args[3].equals("STARTED")) {
                Thread.sleep(60000);
            } else {
                try (var connection = DriverManager.getConnection(args[1], "sa", "");
                     var insert = connection.prepareStatement("insert into TEST_BUSINESS_MARKER(EXECUTION_ID) values (?)")) {
                    insert.setString(1, args[2]);
                    insert.executeUpdate(); // auto-commit 후 Scheduler가 결과를 기록하기 전에 강제 종료된다.
                }
            }
            return;
        }
        String mode = args[0];
        String jdbcUrl = args[1];
        Path ready = Path.of(args[2]);
        var context = new SpringApplicationBuilder(SchedulerApplication.class).web(WebApplicationType.NONE).run(
                "--spring.config.location=classpath:execution-test.yml",
                "--spring.datasource.url=" + jdbcUrl,
                "--spring.jpa.hibernate.ddl-auto=update",
                "--spring.quartz.jdbc.initialize-schema=never",
                "--app.execution.node-id=fault-node",
                "--app.execution.lease-ttl=3s",
                "--app.execution.heartbeat-interval=500ms");
        var coordinator = context.getBean(HistoryExecutionCoordinator.class);
        String id = coordinator.ensureExecution(new HistoryExecutionRequest("test", "fault", ready.getFileName().toString(), "scheduled:1000", Instant.ofEpochMilli(1000), null, "CRON", "SHELL", "0 * * * * ?", "echo test", null));
        LeaseClaim claim = coordinator.claim(id).orElseThrow();
        long pid = -1;
        if (!mode.equals("CLAIMED")) {
            String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
            Process batch = new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"),
                    FaultNodeProcess.class.getName(), "batch", jdbcUrl, id, mode)
                    .redirectErrorStream(true).redirectOutput(ready.resolveSibling(ready.getFileName() + ".batch.log").toFile()).start();
            pid = batch.pid();
            if (mode.equals("COMMITTED")) {
                coordinator.started(claim, pid, batch.info().startInstant().orElse(Instant.now()));
                if (!batch.waitFor(10, TimeUnit.SECONDS) || batch.exitValue() != 0) throw new IllegalStateException("Business process failed");
            }
        }
        Path temporary = ready.resolveSibling(ready.getFileName() + ".tmp");
        Files.writeString(temporary, id + "," + claim.attemptId() + "," + claim.token() + "," + pid);
        Files.move(temporary, ready, StandardCopyOption.ATOMIC_MOVE);
        Thread.sleep(60000); // 부모 테스트가 이 JVM을 강제 종료한다. 정상 종료 훅은 실행하지 않는다.
        context.close();
    }
}
