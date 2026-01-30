package com.example.scheduler.global.api;

public record SingleValueResponse<T>(
    String name,
    T value
) {}
