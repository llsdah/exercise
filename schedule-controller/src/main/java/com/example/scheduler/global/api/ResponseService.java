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
        // 1. 키 가져오기 (예: "success.job.stop")
        String messageKey = code.getMessage();

        // 2. 실제 텍스트로 변환 (예: "작업 중단 요청이 접수되었습니다.")
        String realMessage = messageUtils.getMessage(messageKey);

        // 3. 응답 객체 조립
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .code(code.getCode())
                .message(realMessage)
                .data(data)
                .build();
    }
}