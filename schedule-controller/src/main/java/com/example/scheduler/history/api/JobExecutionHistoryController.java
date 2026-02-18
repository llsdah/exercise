package com.example.scheduler.history.api;

import com.example.scheduler.global.api.ApiResponse;
import com.example.scheduler.global.api.ResponseService;
import com.example.scheduler.global.api.code.SuccessCode;
import com.example.scheduler.history.api.dto.JobExecutionHistoryResponse;
import com.example.scheduler.history.application.JobExecutionHistoryReadService;
import com.example.scheduler.history.domain.JobExecutionHistory;
import com.example.scheduler.history.domain.JobExecutionHistorySearchCondition;
import com.example.scheduler.history.domain.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
public class JobExecutionHistoryController {

    private final JobExecutionHistoryReadService jobExecutionHistoryReadService;
    private final ResponseService responseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobExecutionHistoryResponse>>> getHistories(
            @RequestParam(name = "tenant_id", required = false, defaultValue = "default") String tenantId,
            @RequestParam(name = "schedule_group", required = false) String scheduleGroup,
            @RequestParam(name = "schedule_name", required = false) String scheduleName,
            @RequestParam(name = "fire_instance_id", required = false) String fireInstanceId,
            @RequestParam(name = "status", required = false) ExecutionStatus status, // Enum 자동 매핑
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        JobExecutionHistorySearchCondition condition = new JobExecutionHistorySearchCondition(
                tenantId, scheduleGroup, scheduleName, fireInstanceId, status, from, to
        );

        Page<JobExecutionHistory> historyPage = jobExecutionHistoryReadService.searchHistories(condition, pageable);

        Page<JobExecutionHistoryResponse> result = historyPage.map(JobExecutionHistoryResponse::from);
        return ResponseEntity.ok(
                responseService.success(SuccessCode.SELECT_SUCCESS, result)
        );
    }

}