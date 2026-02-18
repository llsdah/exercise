package com.example.scheduler.global.error;

import com.example.scheduler.global.api.ApiResponse;
import com.example.scheduler.global.api.code.ErrorCode;
import com.example.scheduler.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageUtils messageUtils;

    /**
     * [비즈니스 예외 처리]
     * 개발자가 의도적으로 throw new BusinessException(...) 한 경우
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();

        // 1. 키 가져오기 (예: "error.skip.slot.full")
        String messageKey = code.getMessage();

        // 2. 실제 텍스트로 변환 (예: "스킵 예약 슬롯(5개)이 모두 꽉 찼습니다.")
        String realMessage = messageUtils.getMessage(messageKey);

        log.warn("Business Exception: [{}] {}", code.getCode(), realMessage);

        return ResponseEntity.status(code.getHttpStatus())
                .body(ApiResponse.error(code.getCode(), realMessage));
    }

    /**
     * [시스템 예외 처리]
     * 예상치 못한 런타임 에러 (NPE 등)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception Occurred", e);

        // 공통 서버 에러 메시지 변환
        ErrorCode internalError = ErrorCode.INTERNAL_SERVER_ERROR;
        String realMessage = messageUtils.getMessage(internalError.getMessage());

        return ResponseEntity
                .internalServerError() // HTTP 500
                .body(ApiResponse.error(internalError.getCode(), realMessage));
    }
}