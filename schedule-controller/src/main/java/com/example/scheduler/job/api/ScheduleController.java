package com.example.scheduler.job.api;

import com.example.scheduler.global.api.ApiResponse;
import com.example.scheduler.global.api.ResponseService;
import com.example.scheduler.global.api.SingleValueResponse;
import com.example.scheduler.global.api.code.SuccessCode;
import com.example.scheduler.job.api.dto.ScheduleRequest;
import com.example.scheduler.job.application.model.JobCommand;
import com.example.scheduler.job.application.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class ScheduleController {

    private final JobService jobService;
    private final JobCommandMapper jobCommandMapper;
    private final ResponseService responseService;

    @PostMapping
    public ResponseEntity<ApiResponse<SingleValueResponse<LocalDateTime>>> registerJob(@RequestBody @Valid ScheduleRequest request) {
        // 1. Mapper에게 변환 위임 (Controller는 흐름만 제어)
        JobCommand command = jobCommandMapper.toCommand(request);

        // 2. Service에게 명령 전달
        LocalDateTime nextFireTime = jobService.registerOrUpdateJob(command);

        return ResponseEntity.ok(responseService.success(SuccessCode.INSERT_SUCCESS,new SingleValueResponse<>("nextFireTime",nextFireTime)));
        //return "Job [" + command.jobName() + "] Registered/Updated Successfully";
    }

    @PostMapping("/{scheduleGroup}/{scheduleName}/kill")
    public String killJob(@PathVariable String jobGroup, @PathVariable String jobName) {
        boolean killed = jobService.killJob(jobGroup, jobName);
        return killed ? "Job [" + jobGroup + " " + jobName + "] has been killed." : "Job [" + jobGroup + " " + jobName + "] is not running or not found.";
    }

    @PostMapping("/{scheduleGroup}/{scheduleName}/run")
    public String runNow(@PathVariable String jobGroup, @PathVariable String jobName) {
        jobService.runJobNow(jobGroup, jobName);
        return "Job [" + jobGroup + " " + jobName + "] execution triggered successfully.";
    }

    @DeleteMapping("/{scheduleGroup}/{scheduleName}")
    public String deleteJob(@PathVariable String jobGroup, @PathVariable String jobName) {
        jobService.deleteJob(jobGroup, jobName);
        return "Job [" + jobGroup + " " + jobName + "] has been deleted successfully.";
    }

}