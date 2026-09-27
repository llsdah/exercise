package com.example.scheduler.lease.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// 수정: 요청 검증은 API DTO에서 처리하고 서비스에는 실행권 모델을 전달한다.
public record ExecutionHeartbeatRequest(@NotBlank String attemptId, @NotBlank String nodeId, @Positive long leaseToken) { }
