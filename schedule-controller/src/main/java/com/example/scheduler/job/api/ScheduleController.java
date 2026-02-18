package com.example.scheduler.job.api;

import com.example.scheduler.global.api.ApiResponse;
import com.example.scheduler.global.api.ResponseService;
import com.example.scheduler.global.api.SingleValueResponse;
import com.example.scheduler.global.api.code.SuccessCode;
import com.example.scheduler.job.api.dto.ScheduleRequest;
import com.example.scheduler.job.api.dto.ScheduleResponse;
import com.example.scheduler.job.application.JobService;
import com.example.scheduler.job.application.model.JobCommand;
import com.example.scheduler.job.domain.Schedule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class ScheduleController {

    private final JobService jobService;
    private final JobCommandMapper jobCommandMapper;
    private final ResponseService responseService;

    /**
     * [작업 등록/수정]
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SingleValueResponse<LocalDateTime>>> registerJob(
            @RequestBody @Valid ScheduleRequest request
    ) {
        JobCommand command = jobCommandMapper.toCommand(request);
        LocalDateTime nextFireTime = jobService.registerOrUpdateJob(command);

        return ResponseEntity.ok(
                responseService.success(
                        SuccessCode.INSERT_SUCCESS,
                        new SingleValueResponse<>("nextFireTime", nextFireTime)
                )
        );
    }

    /**
     * [작업 강제 종료]
     */
    @PostMapping("/{scheduleGroup}/{scheduleName}/kill")
    public ResponseEntity<ApiResponse<SingleValueResponse<String>>> killJob(
            @PathVariable("scheduleGroup") String scheduleGroup,
            @PathVariable("scheduleName") String scheduleName,
            @RequestParam(name = "tenant_id", required = false, defaultValue = "default") String tenantId
    ) {

        tenantId = tenantId.toLowerCase();
        scheduleGroup = scheduleGroup.toLowerCase();
        scheduleName = scheduleName.toLowerCase();

        boolean killed = jobService.killJob(tenantId, scheduleGroup, scheduleName);

        String message = killed
                ? "Job [" + scheduleGroup + " " + scheduleName + "] has been killed."
                : "Job is already stopped.";

        return ResponseEntity.ok(
                responseService.success(
                        SuccessCode.UPDATE_SUCCESS,
                        new SingleValueResponse<>("result", message)
                )
        );
    }

    /**
     * [작업 즉시 실행]
     */
    @PostMapping("/{scheduleGroup}/{scheduleName}/run")
    public ResponseEntity<ApiResponse<SingleValueResponse<String>>> runNow(
            @PathVariable("scheduleGroup") String scheduleGroup,
            @PathVariable("scheduleName") String scheduleName,
            @RequestParam(name = "tenant_id", required = false, defaultValue = "default") String tenantId
    ) {

        tenantId = tenantId.toLowerCase();
        scheduleGroup = scheduleGroup.toLowerCase();
        scheduleName = scheduleName.toLowerCase();

        jobService.runJobNow(tenantId, scheduleGroup, scheduleName);

        return ResponseEntity.ok(
                responseService.success(
                        SuccessCode.UPDATE_SUCCESS,
                        new SingleValueResponse<>("result", "Execution triggered successfully.")
                )
        );
    }

    /**
     * [작업 삭제]
     */
    @DeleteMapping("/{scheduleGroup}/{scheduleName}")
    public ResponseEntity<ApiResponse<SingleValueResponse<String>>> deleteJob(
            @PathVariable("scheduleGroup") String scheduleGroup,
            @PathVariable("scheduleName") String scheduleName,
            @RequestParam(value = "tenantId", required = false, defaultValue = "default") String tenantId
    ) {

        tenantId = tenantId.toLowerCase();
        scheduleGroup = scheduleGroup.toLowerCase();
        scheduleName = scheduleName.toLowerCase();

        jobService.deleteJob(tenantId, scheduleGroup, scheduleName);

        return ResponseEntity.ok(
                responseService.success(
                        SuccessCode.DELETE_SUCCESS,
                        new SingleValueResponse<>("result", "Deleted successfully.")
                )
        );
    }

    /**
     * [작업 목록 조회]
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScheduleResponse>>> getSchedules(
            @RequestParam(name = "tenant_id", required = false, defaultValue = "default") String tenantId,
            @RequestParam(name = "schedule_group", required = false) String scheduleGroup,
            @RequestParam(name = "schedule_name", required = false) String scheduleName,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        tenantId = tenantId.toLowerCase();
        scheduleGroup = scheduleGroup != null ? scheduleGroup.toLowerCase() : null;
        scheduleName = scheduleName != null ? scheduleName.toLowerCase() : null;

        Page<Schedule> schedules = jobService.getSchedules(tenantId, scheduleGroup, scheduleName, from, to, pageable);
        return ResponseEntity.ok(responseService.success(
                SuccessCode.SELECT_SUCCESS,
                schedules.map(ScheduleResponse::from) // 깔끔한 참조
        ));
    }

}