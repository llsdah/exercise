package com.example.scheduler.global.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 스케줄러 설정
 */
@ConfigurationProperties(prefix = "app.scheduler")
@Validated
public record SchedulerProperties(
    
    /**
     * Job 등록/삭제 시 Meta DB 사용 여부
     * true: DB에 저장/삭제
     * false: Quartz만 사용
     */
    @NotNull 
    Boolean useMetaTable,
    
    /**
     * Job 실행 시 Meta DB 조회 여부
     * true: 실행 전 DB에서 Job 존재/활성화 체크
     * false: 체크 없이 바로 실행
     */
    @NotNull 
    Boolean checkMetaOnExecute,
    
    /**
     * Skip 최대 등록 개수
     */
    @Min(1) @Max(100) 
    Integer maxSkipCount,
    
    /**
     * Job 실행 타임아웃 (초)
     */
    @Min(60) 
    Long timeoutSeconds
    
) {
    // 기본값 적용 생성자
    public SchedulerProperties {
        if (maxSkipCount == null) {
            maxSkipCount = 5;
        }
        if (timeoutSeconds == null) {
            timeoutSeconds = 3600L;
        }
    }
}