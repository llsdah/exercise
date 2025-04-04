package com.fastcampus.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT,"User name is duplicated"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Internal server error"),
    USER_NOT_FOUNDES(HttpStatus.NOT_FOUND,"User not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"password is invalid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"Token is invalid"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND,"Post is not founded"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED,"permission denied"),
    ALREADY_LIKES(HttpStatus.CONFLICT, "already likes"),


    TEMP(HttpStatus.BAD_REQUEST,"Temp error")
    ;

    private HttpStatus status;
    private String message;
}
