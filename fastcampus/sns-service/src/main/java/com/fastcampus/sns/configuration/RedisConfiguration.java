package com.fastcampus.sns.configuration;

import com.fastcampus.sns.model.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties properties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = RedisURI.create(properties.getUrl());
        org.springframework.data.redis.connection.RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        return factory;
    }

    // redis template = redis 명령어를 쉽게 사용할 수 있게 도와주는 클래스
    // k 키 v 값
    // 데이터의 변경이 많은 경우 캐싱이 의미가 퇴색됩니다. -> DB의 값이 결론적으로 변경해야되기 때문에
    // 변화는 없고, 자주 사용하는 데이터를 캐싱합니다.


    @Bean
    public RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();

        // 명령어 호출을 위한 Connenction 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));

        return redisTemplate;
    }


}


/**
 RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
 config.setHostName(properties.getHost());
 config.setPort(properties.getPort());
 config.setPassword(properties.getPassword()); // 비밀번호 설정
 if (properties.getUsername() != null) {
 config.setUsername(properties.getUsername()); // Redis 6.0+ User 설정
 }

 config.setDatabase(properties.getDatabase());// DB Index 설정

 log.debug("redis connection");

 */