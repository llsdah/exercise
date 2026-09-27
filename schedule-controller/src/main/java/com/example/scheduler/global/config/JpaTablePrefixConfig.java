package com.example.scheduler.global.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaTablePrefixConfig {

    @Bean
    HibernatePropertiesCustomizer tablePrefixHibernatePropertiesCustomizer(
            @Value("${app.jpa.table-prefix:MY_}") String tablePrefix
    ) {
        TablePrefixPhysicalNamingStrategy namingStrategy =
                new TablePrefixPhysicalNamingStrategy(tablePrefix);

        return properties -> properties.put(
                AvailableSettings.PHYSICAL_NAMING_STRATEGY,
                namingStrategy
        );
    }
}
