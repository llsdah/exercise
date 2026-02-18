package com.example.scheduler.job.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ScheduleSkipRequest(
        String tenantId,
        @NotBlank(message = "스케줄 그룹은 필수입니다")
        String scheduleGroup,
        @NotBlank(message = "스케줄 이름은 필수입니다")
        String scheduleName,
        @NotNull(message = "스킵 시간은 필수입니다")
        @Future(message = "스킵 시간은 미래여야 합니다")
        LocalDateTime fireTime,
        @NotNull(message = "등록 사용자는 필수입니다")
        String regUserId
) {}
