package com.example.scheduler.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

    SUCCESS("S000", "success.common"),
    SELECT_SUCCESS("S001", "success.select"),
    INSERT_SUCCESS("S002", "success.insert"),
    UPDATE_SUCCESS("S003", "success.update"),
    DELETE_SUCCESS("S004", "success.delete"),

    SKIP_REGISTERED("S100", "success.skip.registered"),
    JOB_STOP_REQUESTED("S101", "success.job.stop");

    private final String code;
    private final String messageKey; // 변수명: message -> messageKey

    @Override
    public String getMessage() {
        return messageKey;
    }
}