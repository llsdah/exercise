package com.example.scheduler.job.application.model;

import java.time.LocalDateTime;

/**
 * [Service Command]
 * Controller에서 Service로 넘기는 '불변' 데이터 객체입니다.
 * - Map<String, Object> 파라미터는 이미 JSON String으로 변환되어 있어야 합니다.
 * - 도메인 용어(Domain Language)를 따릅니다. (scheduleName -> scheduleName)
 */
public record JobCommand(
        // [식별자]
        String tenantId,
        String jobGroup, // Request의 scheduleGroup
        String jobName,  // Request의 scheduleName

        // [메타데이터]
        String scheduleType,
        String jobType,
        String description, // [추가]

        // [실행 정보]
        String cronExpression,
        String command,
        String parameters, // [중요] JSON String 상태

        // [일정]
        LocalDateTime startTime,
        LocalDateTime endTime,

        // [요청자]
        String regUserId
) {
    // 필요 시 정적 팩토리 메서드 추가 가능
}