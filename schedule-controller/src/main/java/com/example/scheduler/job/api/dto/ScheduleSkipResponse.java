package com.example.scheduler.job.api.dto;

import com.example.scheduler.job.domain.JobSkip;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Builder
public class ScheduleSkipResponse {
    private String tenantId;
    private String scheduleGroup;
    private String scheduleName;
    private LocalDateTime skipTime;

    // [수정] Entity 대신 Domain Model을 파라미터로 받음
    public static ScheduleSkipResponse from(JobSkip domain) {
        return ScheduleSkipResponse.builder()
                .tenantId(domain.getTenantId())
                .scheduleGroup(domain.getScheduleGroup())
                .scheduleName(domain.getScheduleName())
                // 도메인에 있는 getActiveSkipTimes() 같은 편의 메서드 활용
                .skipTime(domain.getSkipTime())
                .build();
    }

}