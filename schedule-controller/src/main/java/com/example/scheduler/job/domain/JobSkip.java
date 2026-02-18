package com.example.scheduler.job.domain;

import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.error.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Builder
public class JobSkip {
    private final String tenantId;
    private final String scheduleGroup;
    private final String scheduleName;
    private final LocalDateTime skipTime;
    private final String regUserId;

    public static JobSkip create(String tenantId, String group, String name,
                                 LocalDateTime skipTime, String regUserId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime truncated = skipTime.truncatedTo(ChronoUnit.MINUTES);

        if (truncated.isBefore(now)) { // "Cannot skip past execution time"
            throw new BusinessException(ErrorCode.SKIP_REGISTER_ERROR_WITH_TIME);
        }

        return JobSkip.builder()
                .tenantId(tenantId)
                .scheduleGroup(group)
                .scheduleName(name)
                .skipTime(skipTime.truncatedTo(ChronoUnit.MINUTES))
                .regUserId(regUserId)
                .build();
    }

}