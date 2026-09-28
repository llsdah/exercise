package com.example.scheduler.dependency.domain;
public class InvalidDependencyException extends IllegalArgumentException {
    public InvalidDependencyException(String message) { super(message); }
}
