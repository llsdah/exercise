package com.fastcampus.sns.model;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;

@Slf4j
@Repository
public class EmitterRepository {

    private Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    public SseEmitter save(Integer id, SseEmitter sseEmitter) {
        final String key = getKey(id);
        sseEmitterMap.put(key,sseEmitter);
        log.info("Set sseEmitter {}", id);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(Integer userId) {
        final String key = getKey(userId);

        log.info("Get sseEmitter {}", userId);
        return Optional.ofNullable(sseEmitterMap.get(userId));
    }

    private String getKey(Integer userId) {
        return "Emitter:UID" + userId;
    }


    public void delete(Integer userId){
        sseEmitterMap.remove(getKey(userId));
    }

}
