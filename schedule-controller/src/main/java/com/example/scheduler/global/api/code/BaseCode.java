package com.example.scheduler.global.api.code;

public interface BaseCode {
    String name();    // Enum 이름 (예: SUCCESS, INVALID_INPUT)
    String getCode(); // 커스텀 코드 (예: S001, E001)
    String getMessage(); // 메시지 (예: 조회 성공)
}