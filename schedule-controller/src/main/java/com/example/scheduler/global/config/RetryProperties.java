package com.example.scheduler.global.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
@ConfigurationProperties("app.retry")
public record RetryProperties(Integer maxAttempts, Duration backoff, Integer workers) {
    public RetryProperties {
        maxAttempts = maxAttempts == null ? 3 : maxAttempts;
        backoff = backoff == null ? Duration.ofSeconds(30) : backoff;
        workers = workers == null ? 2 : workers;
        if (maxAttempts < 1 || backoff.isNegative() || backoff.compareTo(Duration.ofDays(365)) > 0 || workers < 1 || workers > 100)
            throw new IllegalArgumentException("maxAttempts >= 1, backoff between 0 and 365 days, workers between 1 and 100");
    }
}
