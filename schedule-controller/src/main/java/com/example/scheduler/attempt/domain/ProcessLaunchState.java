package com.example.scheduler.attempt.domain;

/** START_REQUESTED is a write-ahead marker, not proof that the OS created a process. */
public enum ProcessLaunchState {
    NOT_REQUESTED, START_REQUESTED, STARTED, START_FAILED, UNKNOWN
}
