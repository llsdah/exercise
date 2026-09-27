package com.example.scheduler.reconciliation.application;

import com.example.scheduler.reconciliation.domain.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReconciliationService {
    private final ReconciliationRepository repository;
    private final ReconciliationVerifier verifier;

    public ReconciliationService(ReconciliationRepository repository, ObjectProvider<ReconciliationVerifier> verifiers) {
        this.repository = repository;
        this.verifier = verifiers.getIfAvailable(PersistedProcessVerifier::new);
    }

    public List<String> findUnknown(int limit) {
        if (limit < 1 || limit > 500) throw new IllegalArgumentException("limit must be between 1 and 500");
        return repository.findUnknown(limit);
    }

    public Optional<ReconciliationResult> reconcile(String id) {
        return repository.reconcile(id, verifier);
    }

    public Optional<ReconciliationResult> findResult(String id) {
        return repository.findResult(id);
    }

    public List<ReconciliationResult> findResults(String id) { return repository.findResults(id); }
}
