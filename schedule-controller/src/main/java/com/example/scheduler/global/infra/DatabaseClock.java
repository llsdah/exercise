package com.example.scheduler.global.infra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;

/** DB statement time AFTER acquiring the relevant row lock, using the same connection.
 * H2 CURRENT_TIMESTAMP is transaction-start time, so use its current statement's
 * DB timestamp instead. No nested transaction/extra connection under pool locks. */
@Component
public class DatabaseClock {
    @PersistenceContext private EntityManager em;

    @Transactional(propagation = Propagation.MANDATORY)
    public Instant now() {
        var dialect = em.getEntityManagerFactory().unwrap(SessionFactoryImplementor.class).getJdbcServices().getDialect();
        String sql;
        if (dialect instanceof H2Dialect) {
            sql = "select EXECUTING_STATEMENT_START from INFORMATION_SCHEMA.SESSIONS where SESSION_ID = SESSION_ID()";
        } else if (dialect instanceof OracleDialect) {
            sql = "select SYSTIMESTAMP from DUAL";
        } else {
            throw new IllegalStateException("A DB statement clock is required for dialect " + dialect.getClass().getName());
        }
        return ((OffsetDateTime) em.createNativeQuery(sql, OffsetDateTime.class).getSingleResult()).toInstant();
    }
}
