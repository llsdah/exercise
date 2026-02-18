package com.example.scheduler.global.constants;

/**
 * 스케줄러 공통 상수
 */
public final class SchedulerConstants {
    
    private SchedulerConstants() {}
    
    // 기본값
    public static final String DEFAULT_TENANT = "default";
    public static final String DEFAULT_GROUP = "default";
    public static final String SYSTEM_USER = "SYSTEM";
    
    // 키 구분자
    public static final String GROUP_SEPARATOR = "::";
    public static final String TRIGGER_SUFFIX = "_trigger";
    
    // 제한값
    public static final int DEFAULT_MAX_SKIP_COUNT = 5;
    public static final long DEFAULT_TIMEOUT_SECONDS = 3600;
}