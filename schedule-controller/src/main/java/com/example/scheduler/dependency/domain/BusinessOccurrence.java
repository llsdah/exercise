package com.example.scheduler.dependency.domain;

import com.example.scheduler.execution.domain.LogicalExecution;
import java.time.*;
import java.util.Date;
import org.quartz.CronExpression;

/** Match the same original Cron ordinal within a business date, independent of actual start time. */
public record BusinessOccurrence(String key, String reason) {
    public static BusinessOccurrence resolve(LogicalExecution execution, ZoneId businessZone) {
        var scheduled = execution.getScheduledAt();
        if (scheduled == null || ! ("scheduled:" + scheduled.toEpochMilli()).equals(execution.getOccurrenceKey()))
            return new BusinessOccurrence(null, "Manual/recovery occurrence has no confirmed business date");
        try {
            var cron = new CronExpression(execution.getCronExpression());
            if (scheduled.toEpochMilli() % 1000 != 0 || !cron.isSatisfiedBy(Date.from(scheduled)))
                return new BusinessOccurrence(null, "Synthetic misfire time is not an original Cron occurrence");
            var day = scheduled.atZone(businessZone).toLocalDate();
            var start = day.atStartOfDay(businessZone).toInstant();
            var first = cron.getNextValidTimeAfter(Date.from(start.minusSeconds(1)));
            int ordinal = 1;
            while (first != null && first.toInstant().isBefore(scheduled)) {
                first = cron.getNextValidTimeAfter(first);
                ordinal++;
            }
            if (first == null || !first.toInstant().equals(scheduled))
                return new BusinessOccurrence(null, "Cannot locate original Cron occurrence");
            return new BusinessOccurrence("BUSINESS_CYCLE_V2|" + businessZone.getId() + "|" + day + "|" + ordinal, null);
        } catch (java.text.ParseException | IllegalArgumentException invalid) {
            return new BusinessOccurrence(null, "Cannot determine business occurrence from Cron expression");
        }
    }
    public static Instant start(String key) {
        var parts = key.split("\\|", -1);
        return LocalDate.parse(parts[2]).atStartOfDay(ZoneId.of(parts[1])).toInstant();
    }
    public static Instant end(String key) {
        var parts = key.split("\\|", -1);
        return LocalDate.parse(parts[2]).plusDays(1).atStartOfDay(ZoneId.of(parts[1])).toInstant();
    }
}
