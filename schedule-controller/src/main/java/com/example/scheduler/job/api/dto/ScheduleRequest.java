package com.example.scheduler.job.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;

public record ScheduleRequest(
        // [식별자 - Composite Key]
        @NotBlank(message = "Need To Tenant ID")
        String tenantId,
        @NotBlank(message = "Need To Schedule Group")
        String scheduleGroup,
        @NotBlank(message = "Need To Schedule Name")
        String scheduleName,
        // [메타데이터]
        String scheduleType, // 예: BATCH, EVENT
        String description,  // [추가] 설명 필드
        // [실행 정보]
        @NotBlank(message = "Job Type은 필수입니다.") // CRON, ONCE, SHELL 등
        String jobType,

        // Cron 표현식 (ONCE가 아닐 경우 필수 유효성 검사 필요 - 커스텀 어노테이션 권장)
        String cronExpression,

        @NotBlank(message = "Command는 필수입니다.")
        String command,

        // 프론트엔드는 JSON 객체로 전달 -> Service로 넘어갈 때 String(JSON)으로 변환됨
        Map<String, Object> parameters,

        // [요청자 정보]
        @NotBlank(message = "요청자 ID는 필수입니다.")
        String regUserId,

        // [시간 정보]
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime scheduleStartTime,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime scheduleEndTime
) {}