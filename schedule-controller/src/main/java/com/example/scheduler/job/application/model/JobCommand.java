package com.example.scheduler.job.application.model;

import java.time.LocalDateTime;

/**
 * [Service DTO]
 * 서비스가 비즈니스 로직을 수행하기 위해 필요한 순수 데이터 묶음입니다.
 * 웹 요청의 복잡성(JSON 구조, 유효성 검사 등)이 모두 제거된 상태입니다.
 */
public record JobCommand(
    String jobGroup,
    String jobName,
    String scheduleType,
    String jobType,
    String jobUser,
    String cronExpression, // null이면 "수동 실행(ONCE)"으로 간주
    String command,
    String parameters, // JSON String으로 변환 완료된 상태

    LocalDateTime startTime,
    LocalDateTime endTime
) {}