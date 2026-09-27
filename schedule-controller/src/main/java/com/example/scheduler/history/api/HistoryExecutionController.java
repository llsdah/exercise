package com.example.scheduler.history.api;

import com.example.scheduler.attempt.api.dto.AttemptResponse;
import com.example.scheduler.lease.api.dto.ExecutionHeartbeatRequest;
import com.example.scheduler.history.api.dto.ExecutionResponse;
import com.example.scheduler.history.application.HistoryExecutionCoordinator;
import com.example.scheduler.lease.domain.LeaseClaim;
import com.example.scheduler.lease.domain.StaleExecutorException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

// 수정: API는 application 서비스만 호출하고 도메인 조회 결과를 DTO로 변환한다.
@RestController
@RequestMapping({"/api/histories/executions", "/api/executions"})
@RequiredArgsConstructor
public class HistoryExecutionController {
    private final HistoryExecutionCoordinator coordinator;

    @GetMapping("/{id}")
    public ResponseEntity<ExecutionResponse> get(@PathVariable("id") String id) {
        return ResponseEntity.of(coordinator.findExecution(id).map(ExecutionResponse::from));
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<AttemptResponse>> attempts(@PathVariable("id") String id) {
        if (coordinator.findExecution(id).isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(coordinator.attempts(id).stream().map(AttemptResponse::from).toList());
    }

    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<Void> heartbeat(@PathVariable("id") String id, @RequestBody @Valid ExecutionHeartbeatRequest request) {
        coordinator.heartbeat(new LeaseClaim(id, request.attemptId(), request.nodeId(), request.leaseToken()));
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(StaleExecutorException.class)
    public ResponseEntity<Map<String, String>> staleExecutor() {
        return ResponseEntity.status(409).body(Map.of("code", "STALE_EXECUTOR"));
    }
}
