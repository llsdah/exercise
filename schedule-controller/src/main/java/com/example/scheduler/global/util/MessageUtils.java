package com.example.scheduler.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUtils {
    private final MessageSource messageSource;
    // 키를 받아 실제 메시지로 변환 (Locale 자동 감지)
    public String getMessage(String key) {
        try {
            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            // 키에 해당하는 메시지가 없으면 키 자체를 반환 (디버깅용)
            return key;
        }
    }
    
    // 파라미터가 있는 메시지 처리용 (예: "{0}번 작업 오류")
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}