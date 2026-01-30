package com.example.scheduler.history.api;

import com.example.scheduler.history.api.dto.JobHistoryResponse;
import com.example.scheduler.history.application.JobHistoryReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
public class JobHistoryController {

    private final JobHistoryReadService jobHistoryReadService;

    /**
     * 이력 목록 조회 API
     * * [사용 예시]
     * 1. 전체 조회 (최신순 10개): GET /api/histories
     * 2. 특정 Job 조회: GET /api/histories?group=DEFAULT&name=backup-job
     * 3. 페이징: GET /api/histories?page=1&size=50
     */
    @GetMapping
    public ResponseEntity<Page<JobHistoryResponse>> getHistories(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String name,
            // 기본값: 시작 시간 기준 내림차순 (최신이 먼저)
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<JobHistoryResponse> responses = jobHistoryReadService.searchHistories(group, name, pageable);
        return ResponseEntity.ok(responses);
    }
}