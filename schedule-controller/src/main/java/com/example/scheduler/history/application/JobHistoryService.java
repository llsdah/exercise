package com.example.scheduler.history.application;

import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.history.domain.JobHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class JobHistoryService {

    private final JobHistoryRepository jobHistoryRepository;

    // 로그 저장은 본 로직이 실패해도 반드시 남아야 하므로 트랜잭션 분리 (REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordHistory(JobHistoryCommand command) {

        // 1. 해당 그룹+이름의 작업이 몇 번째 실행인지 카운트
        long count = jobHistoryRepository.countByScheduleGroupAndScheduleName(command.jobGroup(), command.jobName()) + 1;

        // 2. 엔티티 생성
        JobExecutionHistory history = JobExecutionHistory.builder()
                .scheduleGroup(command.jobGroup())
                .scheduleName(command.jobName())
                .scheduleType(command.scheduleType())
                .jobType(command.jobType())
                .executionCount(count)
                .command(command.fullCommand())
                .status(command.status()) // Enum 상태 저장
                .startTime(command.startTime())
                .endTime(command.endTime())
                .message(command.message())
                .build();

        // 3. 저장
        jobHistoryRepository.save(history);
    }
}