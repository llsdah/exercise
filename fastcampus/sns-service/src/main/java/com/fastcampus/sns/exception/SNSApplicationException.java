package com.fastcampus.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

// TODO : implement
@AllArgsConstructor
@Getter
public class SNSApplicationException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    public SNSApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = "Check Server";
    }

    @Override
    public String getMessage(){
        if (message == null) {
            return errorCode.getMessage();
        }
        return String.format("%s, %s", errorCode.getMessage(), message);
    }

}
