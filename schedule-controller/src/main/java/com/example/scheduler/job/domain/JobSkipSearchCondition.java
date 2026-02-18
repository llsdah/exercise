package com.example.scheduler.job.domain;

import java.time.LocalDateTime;

/**
 * JobSkip 조회를 위한 내부 전용 검색 조건 객체
 */
public record JobSkipSearchCondition(
    String tenantId,
    String scheduleGroup,
    String scheduleName,
    LocalDateTime from,
    LocalDateTime to
) {

    // Compact Constructor로 기본값 처리
    public JobSkipSearchCondition {
        if (from == null) {
            from = LocalDateTime.now();
        }
        if (to == null) {
            to = from.plusDays(60);  // 기본 60일 범위?
        }
        if (to.isBefore(from)) { // 향후 business 변경
            throw new IllegalArgumentException("'to' must be after 'from'");
        }
    }

    public static JobSkipSearchCondition of(String tenantId, String scheduleGroup, String scheduleName, 
                                            LocalDateTime from, LocalDateTime to) {
        return new JobSkipSearchCondition(tenantId, scheduleGroup, scheduleName, from, to);
    }
}