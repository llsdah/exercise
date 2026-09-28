package com.example.scheduler.execution.api;

import com.example.scheduler.execution.application.ExecutionCoordinator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Compatibility for previously documented clients. Prefer /api/executions. */
@Deprecated(forRemoval = false)
@RestController
@RequestMapping("/api/histories/executions")
public class LegacyExecutionController extends ExecutionController {
    public LegacyExecutionController(ExecutionCoordinator coordinator) { super(coordinator); }
}
