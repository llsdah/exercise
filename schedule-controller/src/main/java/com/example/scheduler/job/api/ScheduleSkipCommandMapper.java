package com.example.scheduler.job.api;

import com.example.scheduler.job.api.dto.ScheduleSkipRequest;
import com.example.scheduler.job.application.model.JobSkipCommand;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ScheduleSkipCommandMapper {

    public JobSkipCommand toCommand(ScheduleSkipRequest req) {
        return new JobSkipCommand(
                normalizeTenant(req.tenantId()),
                normalize(req.scheduleGroup()),
                normalize(req.scheduleName()),
                req.fireTime(),
                req.regUserId()

        );
    }

    private String normalize(String v) {
        return v == null ? null : v.trim().toLowerCase();
    }

    private String normalizeTenant(String tenantId) {
        return StringUtils.hasText(tenantId) ? tenantId.trim().toLowerCase() : "default";
    }
}
