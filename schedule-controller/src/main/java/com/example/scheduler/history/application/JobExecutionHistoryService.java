package com.example.scheduler.history.application;

import com.example.scheduler.history.domain.JobExecutionHistory;
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

        // 1. 해당 Tenant + 그룹 + 이름의 작업이 몇 번째 실행인지 카운트
        // 로그 삭제 대비해 최댓값 조회
        long count = jobHistoryRepository.findMaxExecutionCount(
                command.tenantId(),
                command.jobGroup(),
                command.jobName()
        ) + 1;

        // 2. 도메인/엔티티 생성
        JobExecutionHistory history = JobExecutionHistory.builder()
                .tenantId(command.tenantId())        // [신규]
                .scheduleGroup(command.jobGroup())
                .scheduleName(command.jobName())
                .fireInstanceId(command.fireInstanceId())
                .scheduleType(command.scheduleType())
                .jobType(command.jobType())
                .jobId(command.jobId())              // [신규]
                .executionCount(count)
                .cronExpression(command.cronExpression())
                .parameters(command.parameters())
                .command(command.command())
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