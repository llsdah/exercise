package com.example.scheduler.global.config;

import org.hibernate.boot.model.naming.Identifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class TablePrefixPhysicalNamingStrategyTest {

    @Test
    void addsConfiguredPrefixToApplicationTable() {
        var strategy = new TablePrefixPhysicalNamingStrategy("MY_");

        Identifier result = strategy.toPhysicalTableName(
                Identifier.toIdentifier("SCHEDULE_INFO"),
                null
        );

        assertThat(result.getText()).isEqualTo("MY_SCHEDULE_INFO");
    }

    @Test
    void doesNotAddPrefixTwice() {
        var strategy = new TablePrefixPhysicalNamingStrategy("MY_");

        Identifier result = strategy.toPhysicalTableName(
                Identifier.toIdentifier("MY_SCHEDULE_INFO"),
                null
        );

        assertThat(result.getText()).isEqualTo("MY_SCHEDULE_INFO");
    }

    @Test
    void rejectsUnsafePrefix() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TablePrefixPhysicalNamingStrategy("MY-"));
    }
}
