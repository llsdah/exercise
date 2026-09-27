package com.example.scheduler.history;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import java.sql.DriverManager;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class HistoryMigrationTest {
    @Test
    void migrationPreservesEveryLegacyColumnAndRowAndCopiesExecutionIdentity() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:migration-" + UUID.randomUUID(), "sa", "")) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("history-legacy-schema.sql"));
            Set<String> columns = new HashSet<>();
            try (var rs = connection.getMetaData().getColumns(null, "PUBLIC", "MY_SCHEDULE_EXECUTION_HISTORY", null)) {
                while (rs.next()) columns.add(rs.getString("COLUMN_NAME"));
            }
            try (var sql = connection.createStatement()) {
                sql.executeUpdate("insert into MY_SCHEDULE_EXECUTION_HISTORY (TENANT_ID,SCHEDULE_GROUP,SCHEDULE_NAME,SCHEDULE_TYPE,JOB_TYPE,CRON_EXPRESSION,COMMAND,STATUS,START_TIME,MESSAGE,EXECUTION_COUNT) "
                        + "values ('t','g','job','CRON','SHELL','0 * * * * ?','echo legacy','SUCCESS',TIMESTAMP '2026-09-25 12:00:00','original log',42)");
                sql.executeUpdate("update MY_SCHEDULE_EXECUTION_HISTORY set FIRE_INSTANCE_ID='legacy-fire', JOB_ID='legacy-job', "
                        + "PARAMETERS='original parameters', END_TIME=TIMESTAMP '2026-09-25 12:00:01', DURATION=1000");
                sql.executeUpdate("insert into MY_BATCH_EXECUTION (EXECUTION_ID,TENANT_ID,SCHEDULE_GROUP,SCHEDULE_NAME,OCCURRENCE_KEY,SCHEDULED_AT,STATUS,ATTEMPT_COUNT) "
                        + "values ('00000000-0000-0000-0000-000000000001','t','g','job','scheduled:1000',CURRENT_TIMESTAMP,'RUNNING',1)");
                sql.executeUpdate("insert into MY_BATCH_EXECUTION_LEASE (EXECUTION_ID,OWNER_NODE,LEASE_TOKEN,EXPIRES_AT) "
                        + "values ('00000000-0000-0000-0000-000000000001','node-a',7,CURRENT_TIMESTAMP)");
                sql.executeUpdate("insert into MY_BATCH_ATTEMPT (ATTEMPT_ID,EXECUTION_ID,ATTEMPT_NO,NODE_ID,LEASE_TOKEN,STARTED_AT,STATUS) "
                        + "values ('attempt-1','00000000-0000-0000-0000-000000000001',1,'node-a',7,CURRENT_TIMESTAMP,'RUNNING')");
            }
            var beforeValues = legacyValues(connection, columns);
            var migration = new FileSystemResource("db/migration/history-unification-h2.sql");
            assertThat(migration.exists()).as("Checked-in H2 migration must exist").isTrue();
            ScriptUtils.executeSqlScript(connection, migration);
            assertThat(legacyValues(connection, columns)).containsExactlyInAnyOrderEntriesOf(beforeValues);
            Set<String> after = new HashSet<>();
            try (var rs = connection.getMetaData().getColumns(null, "PUBLIC", "MY_SCHEDULE_EXECUTION_HISTORY", null)) {
                while (rs.next()) after.add(rs.getString("COLUMN_NAME"));
            }
            assertThat(after).containsAll(columns).hasSize(columns.size() + 6);
            try (var sql = connection.createStatement()) {
                try (var rs = sql.executeQuery("select * from MY_SCHEDULE_EXECUTION_HISTORY where OCCURRENCE_KEY like 'legacy:%'")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString("MESSAGE")).isEqualTo("original log");
                    assertThat(rs.getString("COMMAND")).isEqualTo("echo legacy");
                    assertThat(rs.getLong("EXECUTION_COUNT")).isEqualTo(42);
                    assertThat(rs.getString("STATUS")).isEqualTo("SUCCESS");
                    assertThat(rs.getString("EXECUTION_ID")).hasSize(36);
                    assertThat(rs.next()).isFalse();
                }
                try (var rs = sql.executeQuery("select h.STATUS,h.ATTEMPT_COUNT,l.LEASE_TOKEN from MY_SCHEDULE_EXECUTION_HISTORY h "
                        + "join MY_BATCH_EXECUTION_LEASE l on h.EXECUTION_ID=l.EXECUTION_ID where h.OCCURRENCE_KEY='scheduled:1000'")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString(1)).isEqualTo("RUNNING");
                    assertThat(rs.getInt(2)).isEqualTo(1);
                    assertThat(rs.getLong(3)).isEqualTo(7);
                }
                // A scheduled row can exist before START_TIME is known.
                sql.executeUpdate("insert into MY_SCHEDULE_EXECUTION_HISTORY (EXECUTION_ID,TENANT_ID,SCHEDULE_GROUP,SCHEDULE_NAME,SCHEDULE_TYPE,JOB_TYPE,CRON_EXPRESSION,COMMAND,STATUS) "
                        + "values (RANDOM_UUID(),'t','g','scheduled','CRON','SHELL','0 * * * * ?','echo test','SCHEDULED')");
                try (var rs = sql.executeQuery("select count(*) from MY_BATCH_EXECUTION")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getInt(1)).isEqualTo(1); // Old ledger is retained, not dropped.
                }
            }
        }
    }

    private Map<String, String> legacyValues(java.sql.Connection connection, Set<String> columns) throws Exception {
        var result = new HashMap<String, String>();
        try (var sql = connection.createStatement();
             var row = sql.executeQuery("select * from MY_SCHEDULE_EXECUTION_HISTORY where FIRE_INSTANCE_ID='legacy-fire'")) {
            assertThat(row.next()).isTrue();
            for (String column : columns) result.put(column, row.getString(column)); // Includes CLOB contents and timestamps.
            assertThat(row.next()).isFalse();
        }
        return result;
    }
}
