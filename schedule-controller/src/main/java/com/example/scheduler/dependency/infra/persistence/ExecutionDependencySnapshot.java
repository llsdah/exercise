package com.example.scheduler.dependency.infra.persistence;
import jakarta.persistence.*;
@Entity @Table(name = "BATCH_EXECUTION_DEPENDENCY")
public class ExecutionDependencySnapshot {
    @Id @Column(name = "EXECUTION_ID", length = 36) private String executionId;
    @Lob @Column(name = "DEPENDS_ON", nullable = false) private String dependsOn;
    @Column(name = "BUSINESS_OCCURRENCE_KEY", length = 150) private String businessOccurrenceKey;
    @Column(name = "OCCURRENCE_REASON", length = 500) private String occurrenceReason;
    protected ExecutionDependencySnapshot() { }
    ExecutionDependencySnapshot(String id, String names, com.example.scheduler.dependency.domain.BusinessOccurrence occurrence) {
        executionId=id; dependsOn=names.isEmpty() ? "\n" : names;
        businessOccurrenceKey=occurrence.key(); occurrenceReason=occurrence.reason();
    }
    String names() { return dependsOn.equals("\n") ? "" : dependsOn; }
    String businessOccurrenceKey() { return businessOccurrenceKey; }
    String occurrenceReason() { return occurrenceReason; }
}
