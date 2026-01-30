package com.example.scheduler.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {

    private final LocalDateTime timestamp;
    private final String code;    // 응답 코드 (S001, E400 등)
    private final String message; // 변환된 실제 한글 메시지

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;         // 데이터 Payload


    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}