package com.example.scheduler.job.api;

import com.example.scheduler.global.api.ApiResponse;
import com.example.scheduler.global.api.ResponseService;
import com.example.scheduler.global.api.code.SuccessCode;
import com.example.scheduler.job.api.dto.SchedulerJobDto;
import com.example.scheduler.job.api.dto.SkipScheduleDto;
import com.example.scheduler.job.api.dto.UpcomingJobDto;
import com.example.scheduler.job.application.JobService;
import com.example.scheduler.job.application.SchedulerReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scheduler/monitoring") // URL도 역할에 맞게 변경 (선택사항)
@RequiredArgsConstructor
public class SchedulerMonitoringController {

    private final SchedulerReadService schedulerReadService;
    private final JobService jobService; // 주입 필요
    private final ResponseService responseService; // 👈 포장 담당자

    /**
     * 현재 스케줄러 엔진에 등록된 모든 작업의 실시간 상태 조회
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<SchedulerJobDto>> getRunningJobs() {
        List<SchedulerJobDto> jobs = schedulerReadService.findAllJobs();
        return ResponseEntity.ok(jobs);
    }

    /**
     * 실행 예정 작업 검색 API
     * * [요청 예시]
     * 1. 그룹만 검색: GET /api/scheduler/monitoring/search?group=DEFAULT
     * 2. 이름까지 지정: GET /api/scheduler/monitoring/search?group=DEFAULT&name=backup-job
     * 3. 기간 지정: GET /api/scheduler/monitoring/search?group=DEFAULT&startTime=2026-01-24 10:00:00
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UpcomingJobDto>>> searchSchedule(
            @RequestParam String group, // 필수

            @RequestParam(required = false) String name,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<UpcomingJobDto> list = schedulerReadService.searchUpcomingJobs(group, name, startTime, endTime);
        return ResponseEntity.ok(responseService.success(SuccessCode.SELECT_SUCCESS, list));
    }

    /**
     * 특정 회차 실행 건너뛰기 (Skip Instance)
     * 트리거는 유지되고, 해당 시간의 실행만 취소됩니다.
     * POST /api/scheduler/monitoring/skip?group=..&name=..&time=2026-01-24T21:10:00
     */
    @PostMapping("/skip")
    public ResponseEntity<ApiResponse<SkipScheduleDto>> skipInstance(
            @RequestParam String group,
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time
    ) {
        SkipScheduleDto result = jobService.addSkipSchedule(group, name, time);
        return ResponseEntity.ok(responseService.success(SuccessCode.SKIP_REGISTERED, result));
    }

    /**
     * [조회] 스킵 예약된 작업 목록 조회
     * GET /api/scheduler/monitoring/skips
     */
    @GetMapping("/skips")
    public ResponseEntity<ApiResponse<List<SkipScheduleDto>>> getSkipList() {
        List<SkipScheduleDto> list = schedulerReadService.getPendingSkipSchedules();
        return ResponseEntity.ok(responseService.success(SuccessCode.SELECT_SUCCESS, list));
    }

    /**
     * 스킵 예약 취소 (특정 시간 삭제)
     * DELETE /api/scheduler/monitoring/skip?group=..&name=..&time=2026-01-25T09:00:00
     */
    @DeleteMapping("/skip")
    public ResponseEntity<ApiResponse<Void>> cancelSkip(
            @RequestParam String group,
            @RequestParam String name,
            // 등록 때와 똑같이 ISO 포맷 사용
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time
    ) {
        jobService.cancelSkipSchedule(group, name, time);
        return ResponseEntity.ok(responseService.success(SuccessCode.DELETE_SUCCESS));
    }

}