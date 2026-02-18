package com.example.scheduler.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR("E500", "error.server.internal", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("E400", "error.input.invalid", HttpStatus.BAD_REQUEST),

    // Job 에러
    JOB_NOT_FOUND("J001", "error.job.not.found", HttpStatus.NOT_FOUND),
    JOB_NOT_RUNNING("J002", "error.job.not.running", HttpStatus.CONFLICT),
    JOB_ALREADY_STOPPED("J005", "error.job.already.stopped", HttpStatus.CONFLICT),
    JOB_ALREADY_RUNNING("J006", "error.job.already.running", HttpStatus.CONFLICT),

    // Skip 에러
    SKIP_SLOT_FULL("J003", "error.skip.slot.full", HttpStatus.CONFLICT),
    SKIP_NOT_FOUND("J004", "error.skip.not.found", HttpStatus.NOT_FOUND),
    SKIP_REGISTER_ERROR_WITH_TIME("J007", "error.job.skip.register.time", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String messageKey;
    private final HttpStatus httpStatus;

    @Override
    public String getMessage() {
        return messageKey;
    }
}