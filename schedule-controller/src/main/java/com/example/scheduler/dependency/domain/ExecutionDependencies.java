package com.example.scheduler.dependency.domain;
import com.example.scheduler.execution.domain.LogicalExecution;
public interface ExecutionDependencies {
    void capture(LogicalExecution execution);
    Decision evaluate(LogicalExecution execution);
    record Decision(boolean ready, boolean manualReview, String reason) { }
}
