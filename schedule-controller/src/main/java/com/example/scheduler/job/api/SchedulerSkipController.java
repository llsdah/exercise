package com.example.scheduler.job.api;

import com.example.scheduler.global.api.ApiResponse;
import com.example.scheduler.global.api.ResponseService;
import com.example.scheduler.global.api.code.SuccessCode;
import com.example.scheduler.job.api.dto.JobSkipSearchRequest;
import com.example.scheduler.job.api.dto.ScheduleSkipRequest;
import com.example.scheduler.job.api.dto.ScheduleSkipResponse;
import com.example.scheduler.job.application.JobSkipService;
import com.example.scheduler.job.application.model.JobSkipCommand;
import com.example.scheduler.job.domain.JobSkip;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/scheduler/skip")
@RequiredArgsConstructor
public class SchedulerSkipController {

    private final JobSkipService jobSkipService;
    private final ResponseService responseService;
    private final ScheduleSkipCommandMapper scheduleSkipCommandMapper;

    /**
     * 특정 회차 실행 건너뛰기 (Skip Instance)
     * 트리거는 유지되고, 해당 시간의 실행만 취소됩니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleSkipResponse>> skipInstance(
            @RequestBody @Valid ScheduleSkipRequest request
    ) {
        JobSkipCommand command = scheduleSkipCommandMapper.toCommand(request);
        JobSkip jobSkip = jobSkipService.addSkipSchedule(command);

        ScheduleSkipResponse response = ScheduleSkipResponse.from(jobSkip);
        return ResponseEntity.ok(
                responseService.success(SuccessCode.SKIP_REGISTERED, response)
        );
    }

    /**
     * [조회] 스킵 예약된 작업 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScheduleSkipResponse>>> getSkipList(
            JobSkipSearchRequest searchRequest,
            @PageableDefault(size = 20) Pageable pageable) {

        // 1. Service는 도메인 모델(JobSkip)의 Page를 반환
        Page<JobSkip> domainPage = jobSkipService.getPendingSkipSchedules(searchRequest, pageable);

        // 2. 최종 API 응답용 DTO로 변환하여 반환
        Page<ScheduleSkipResponse> response = domainPage.map(ScheduleSkipResponse::from);

        return ResponseEntity.ok(
                responseService.success(SuccessCode.SELECT_SUCCESS, response)
        );
    }

    /**
     * 스킵 예약 취소 (특정 시간 삭제)
     */
    @DeleteMapping("/{scheduleGroup}/{scheduleName}")
    public ResponseEntity<ApiResponse<Void>> cancelSkip(
            @PathVariable("scheduleGroup") String scheduleGroup,
            @PathVariable("scheduleName") String scheduleName,
            @RequestParam(name = "tenant_id", required = false, defaultValue = "default") String tenantId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time
    ) {
        jobSkipService.cancelSkipSchedule(tenantId, scheduleGroup, scheduleName, time);

        return ResponseEntity.ok(
                responseService.success(SuccessCode.DELETE_SUCCESS)
        );
    }
}
