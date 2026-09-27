package com.example.scheduler.reconciliation.domain;

import java.util.List;
import java.util.Optional;

public interface ReconciliationRepository {
    List<String> findUnknown(int limit);
    Optional<ReconciliationResult> findResult(String executionId);
    List<ReconciliationResult> findResults(String executionId);
    Optional<ReconciliationResult> reconcile(String executionId, ReconciliationVerifier verifier);
}
