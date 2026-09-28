package com.example.scheduler.retry.api;
import com.example.scheduler.retry.application.RetryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/executions"})
public class RetryController {
    private final RetryService service;
    public record RetryRequest(@NotBlank String attemptId) {}
    @PostMapping("/{id}/retry")
    public RetryService.RetryResult retry(@PathVariable("id") String id, @RequestBody @Valid RetryRequest request) {
        return service.retry(id, request.attemptId());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> missing() { return ResponseEntity.notFound().build(); }
}
