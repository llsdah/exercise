package com.example.scheduler.history.application;

import com.example.scheduler.execution.domain.ExecutionStatus;

import com.example.scheduler.execution.domain.LogicalExecution;
import com.example.scheduler.history.domain.JobExecutionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class JobExecutionHistoryService {

    private final JobExecutionHistoryRepository jobHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordHistory(JobExecutionHistoryCommand command) {

        if (command.status() != com.example.scheduler.execution.domain.ExecutionStatus.SKIPPED
                && command.status() != com.example.scheduler.execution.domain.ExecutionStatus.WARNING) {
            throw new IllegalArgumentException("Process results must update History through a fenced lease");
        }
        // 1. 해당 Tenant + 그룹 + 이름의 작업이 몇 번째 실행인지 카운트
        // 로그 삭제 대비해 최댓값 조회
        long count = jobHistoryRepository.findMaxExecutionCount(
                command.tenantId(),
                command.jobGroup(),
                command.jobName()
        ) + 1;

        // 2. 도메인/엔티티 생성
        LogicalExecution history = LogicalExecution.builder()
                .tenantId(command.tenantId())        // [신규]
                .scheduleGroup(command.jobGroup())
                .scheduleName(command.jobName())
                .fireInstanceId(command.fireInstanceId())
                .scheduleType(command.scheduleType() == null ? "SYSTEM" : command.scheduleType())
                .jobType(command.jobType() == null ? "SYSTEM" : command.jobType())
                .jobId(command.jobId())              // [신규]
                .executionCount(count)
                .cronExpression(command.cronExpression() == null ? "MANUAL" : command.cronExpression())
                .parameters(command.parameters())
                .command(command.command() == null ? "N/A" : command.command())
                .status(command.status())
                .startTime(command.startTime())
                .endTime(command.endTime())
                .duration(command.duration())        // [신규]
                .message(command.message())
                .build();

        // 3. 저장
        jobHistoryRepository.save(history);
    }
}