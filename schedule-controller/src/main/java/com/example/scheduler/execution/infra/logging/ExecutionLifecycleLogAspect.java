package com.example.scheduler.execution.infra.logging;

import com.example.scheduler.reconciliation.infra.persistence.ReconciliationRepositoryImpl;

import com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl;

import com.example.scheduler.global.logging.ExecutionLogContext;
import com.example.scheduler.execution.application.port.ExecutionRepository;
import com.example.scheduler.lease.domain.LeaseClaim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/** Observes committed operations. Diagnostic reads must never change the operation's outcome. */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionLifecycleLogAspect {
    private final org.springframework.beans.factory.ObjectProvider<ExecutionRepository> repositories;

    @Around("execution(public * com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl.ensureExecution(..)) || "
            + "execution(public * com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl.claim*(..)) || "
            + "execution(public * com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl.started(..)) || "
            + "execution(public * com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl.startFailed(..)) || "
            + "execution(public * com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl.deferForDrain(..)) || "
            + "execution(public * com.example.scheduler.execution.infra.persistence.ExecutionRepositoryImpl.finish(..)) || "
            + "execution(public * com.example.scheduler.reconciliation.infra.persistence.ReconciliationRepositoryImpl.reconcile(..))")
    public Object observe(ProceedingJoinPoint invocation) throws Throwable {
        Object result;
        try { result = invocation.proceed(); }
        catch (Throwable failure) {
            record(invocation, null, failure);
            throw failure;
        }
        record(invocation, result, null);
        return result;
    }

    private void record(ProceedingJoinPoint invocation, Object result, Throwable failure) {
        Object argument = invocation.getArgs()[0];
        String id = argument instanceof LeaseClaim claim ? claim.executionId()
                : argument instanceof String value ? value : result instanceof String value ? value : null;
        if (id == null) return;
        LeaseClaim operationClaim = argument instanceof LeaseClaim claim ? claim
                : result instanceof com.example.scheduler.retry.domain.RetryAdmission admission ? admission.claim()
                : result instanceof java.util.Optional<?> optional && optional.orElse(null) instanceof LeaseClaim claim ? claim : null;
        String attemptId = operationClaim == null ? null : operationClaim.attemptId();
        String trigger = null, worker = null;
        Long token = operationClaim == null ? null : operationClaim.token(), pid = null;
        Object status = null;
        try {
            var executions = repositories.getObject();
            var history = executions.findExecution(id).orElse(null);
            if (history != null) { trigger = history.getTriggerNodeId(); status = history.getStatus(); }
            var attempts = executions.attempts(id);
            for (var attempt : attempts) {
                if (operationClaim != null && !operationClaim.attemptId().equals(attempt.getId())) continue;
                if (operationClaim == null && history != null && attempt.getAttemptNo() != history.getAttemptCount()) continue;
                attemptId = attempt.getId(); worker = attempt.getWorkerNodeId();
                token = attempt.getLeaseToken(); pid = attempt.getPid();
            }
        } catch (RuntimeException unavailable) { /* Logging cannot invalidate a committed transition. */ }
        // A local process may have started even if its fenced DB update failed.
        if (id.equals(org.slf4j.MDC.get("executionId")) && java.util.Objects.equals(attemptId, org.slf4j.MDC.get("attemptId"))) {
            if (trigger == null) trigger = org.slf4j.MDC.get("triggerNodeId");
            if (worker == null) worker = org.slf4j.MDC.get("workerNodeId");
            String localPid = org.slf4j.MDC.get("pid");
            if (pid == null && localPid != null) {
                try { pid = Long.valueOf(localPid); } catch (NumberFormatException ignored) { }
            }
        }
        try (var trace = ExecutionLogContext.open(id, attemptId, trigger, worker, token, pid)) {
            if (failure == null) log.info("Lifecycle operation={} completed; observedStatus={}", invocation.getSignature().getName(), status);
            else log.warn("Lifecycle operation={} failed", invocation.getSignature().getName(), failure);
        }
    }
}
