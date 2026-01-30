package com.example.scheduler.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // [필수] 이거 없으면 시간 안 들어감!
public class JpaConfig {
}