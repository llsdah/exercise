package com.example.scheduler.global.logging;

import org.slf4j.MDC;
import java.util.HashMap;
import java.util.Map;

/** A scoped snapshot; always restores the caller's MDC, including on pooled threads. */
public final class ExecutionLogContext implements AutoCloseable {
    private final Map<String, String> previous = MDC.getCopyOfContextMap();

    private ExecutionLogContext(Map<String, String> values) {
        MDC.clear();
        if (values != null) MDC.setContextMap(values);
    }

    public static ExecutionLogContext restore(Map<String, String> values) {
        return new ExecutionLogContext(values);
    }

    public static ExecutionLogContext open(String executionId, String attemptId, String triggerNodeId,
                                            String workerNodeId, Long leaseToken, Long pid) {
        var values = MDC.getCopyOfContextMap();
        if (values == null) values = new HashMap<>();
        put(values, "executionId", executionId);
        put(values, "attemptId", attemptId);
        put(values, "triggerNodeId", triggerNodeId);
        put(values, "workerNodeId", workerNodeId);
        put(values, "leaseToken", leaseToken);
        put(values, "pid", pid);
        return restore(values);
    }

    private static void put(Map<String, String> values, String key, Object value) {
        if (value == null) values.remove(key); else values.put(key, value.toString());
    }

    public static Runnable propagate(Runnable task) {
        var snapshot = MDC.getCopyOfContextMap();
        return () -> { try (var ignored = restore(snapshot)) { task.run(); } };
    }

    @Override public void close() {
        MDC.clear();
        if (previous != null) MDC.setContextMap(previous);
    }
}
