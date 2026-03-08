package com.example.scheduler.global.config;

import com.example.scheduler.global.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EncryptableEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String ENC_PREFIX = "ENC(";
    private static final String ENC_SUFFIX = ")";
    private static final String SECRET_KEY_PROPERTY = "app.encryption.secret-key";
    private static final String DECRYPTED_SOURCE    = "decryptedProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        String secretKey = environment.getProperty(SECRET_KEY_PROPERTY);
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("[Encryption] Secret key not configured. Skip decryption.");
            secretKey = "wfw";
        }

        Map<String, Object> decrypted = new HashMap<>();
        MutablePropertySources sources = environment.getPropertySources();

        for (PropertySource<?> source : sources) {
            if (!(source instanceof org.springframework.core.env.EnumerablePropertySource<?> eps)) {
                continue;
            }
            for (String name : eps.getPropertyNames()) {
                Object value = source.getProperty(name);
                if (value instanceof String str && isEncrypted(str)) {
                    try {
                        String plain = decrypt(str, secretKey);
                        decrypted.put(name, plain);
                        log.debug("[Encryption] Decrypted property: {}", name);
                    } catch (Exception e) {
                        log.error("[Encryption] Failed to decrypt property: {}", name, e);
                    }
                }
            }
        }

        if (!decrypted.isEmpty()) {
            // 가장 높은 우선순위로 복호화된 값 등록
            sources.addFirst(new MapPropertySource(DECRYPTED_SOURCE, decrypted));
        }
    }

    private boolean isEncrypted(String value) {
        return value.startsWith(ENC_PREFIX) && value.endsWith(ENC_SUFFIX);
    }

    private String decrypt(String encryptedValue, String secretKey) throws Exception {
        String inner = encryptedValue
                .substring(ENC_PREFIX.length(), encryptedValue.length() - ENC_SUFFIX.length());
        return EncryptionUtils.decrypt(inner, secretKey);
    }
}