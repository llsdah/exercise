package com.example.scheduler.job.application.schedule;

import com.example.scheduler.global.constants.SchedulerConstants;

public final class ScheduleKeyPolicy {


    private ScheduleKeyPolicy() {}

    public static String jobGroup(String tenantId, String group) {
        return tenantId + SchedulerConstants.GROUP_SEPARATOR + group;
    }

    public static String triggerName(String jobName) {
        return jobName + SchedulerConstants.TRIGGER_SUFFIX;
    }

    public static String extractTenantId(String compositeGroupKey) {
        String[] parts = split(compositeGroupKey);
        return parts[0];
    }

    public static String extractGroup(String compositeGroupKey) {
        String[] parts = split(compositeGroupKey);
        return parts[1];
    }

    private static String[] split(String compositeGroupKey) {
        if (compositeGroupKey == null || !compositeGroupKey.contains(SchedulerConstants.GROUP_SEPARATOR)) {
            throw new IllegalArgumentException(
                    "Invalid groupKey format. Expected: {tenantId}::{scheduleGroup}"
            );
        }
        return compositeGroupKey.split(SchedulerConstants.GROUP_SEPARATOR, 2);
    }
}
