package com.example.scheduler.global.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Adds the configured prefix to application-owned JPA tables.
 * Quartz tables are managed separately by Quartz's tablePrefix property.
 */
public class TablePrefixPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private final String tablePrefix;

    public TablePrefixPhysicalNamingStrategy(String tablePrefix) {
        if (tablePrefix == null || !tablePrefix.matches("[A-Za-z][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException(
                    "app.jpa.table-prefix must start with a letter and contain only letters, digits, or underscores"
            );
        }
        this.tablePrefix = tablePrefix;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        if (logicalName == null) {
            return null;
        }

        String physicalName = logicalName.getText().startsWith(tablePrefix)
                ? logicalName.getText()
                : tablePrefix + logicalName.getText();

        return Identifier.toIdentifier(physicalName, logicalName.isQuoted());
    }
}
