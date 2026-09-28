package com.example.scheduler.reconciliation.api;

import com.example.scheduler.reconciliation.application.ReconciliationService;
import com.example.scheduler.reconciliation.domain.ReconciliationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/executions"})
public class ReconciliationController {
    private final ReconciliationService service;

    @GetMapping("/unknown")
    public List<String> unknown(@RequestParam(name = "limit", defaultValue = "100") int limit) {
        return service.findUnknown(limit);
    }

    @PostMapping("/{id}/reconciliation")
    public ResponseEntity<ReconciliationResult> reconcile(@PathVariable("id") String id) {
        return ResponseEntity.of(service.reconcile(id));
    }

    @GetMapping("/{id}/reconciliation")
    public ResponseEntity<ReconciliationResult> result(@PathVariable("id") String id) {
        return ResponseEntity.of(service.findResult(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> invalidArgument() {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}/reconciliations")
    public List<ReconciliationResult> results(@PathVariable("id") String id) {
        return service.findResults(id);
    }
}
