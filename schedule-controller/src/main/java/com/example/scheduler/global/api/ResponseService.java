package com.example.scheduler.global.api;

import com.example.scheduler.global.api.code.BaseCode;
import com.example.scheduler.global.api.code.SuccessCode;
import com.example.scheduler.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResponseService {
    private final MessageUtils messageUtils;

    public <T> ApiResponse<T> success(T data) {
        return success(SuccessCode.SUCCESS, data);
    }

    public <T> ApiResponse<T> success(BaseCode code) {
        return success(code, null);
    }

    public <T> ApiResponse<T> success(BaseCode code, T data) {
        String realMessage = messageUtils.getMessage(code.getMessage());
        return ApiResponse.success(code.getCode(), realMessage, data);
    }

    // 에러도 통일
    public <T> ApiResponse<T> error(BaseCode code) {
        String realMessage = messageUtils.getMessage(code.getMessage());
        return ApiResponse.error(code.getCode(), realMessage);
    }
}