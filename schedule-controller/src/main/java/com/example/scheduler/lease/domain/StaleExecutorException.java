package com.example.scheduler.lease.domain;

// 수정: 만료되었거나 이전 실행자가 보낸 쓰기를 명시적으로 거부한다.
public class StaleExecutorException extends RuntimeException {
    public StaleExecutorException() {
        super("STALE_EXECUTOR");
    }
}
