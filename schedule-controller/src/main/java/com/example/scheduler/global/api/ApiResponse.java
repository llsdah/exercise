package com.example.scheduler.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {
    private final LocalDateTime timestamp;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    // 성공 응답
    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    // 에러 응답
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    // 성공 여부 판단 헬퍼
    public boolean isSuccess() {
        return code != null && code.startsWith("S");
    }
}