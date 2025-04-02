package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofHours(1);

    public void setUser(User user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis : {}, {}",key,user);
        userRedisTemplate.opsForValue().set(key, user);
    }

    public Optional<User> getUser(String username) {
        String key = getKey(username);
        User user = userRedisTemplate.opsForValue().get(getKey(username));
        log.info("Get User to Redis : {}, {}",key, user);
        return Optional.ofNullable(user);
    }

    // 현재는 username으로 db를 가져온다
    private String getKey(String username) {
        // 향후 키에 대한 다양성을 고려해 prefix를 붙인다.
        return "USER:" + username;
    }
}


