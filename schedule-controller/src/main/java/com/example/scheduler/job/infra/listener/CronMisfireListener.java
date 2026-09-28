package com.example.scheduler.job.infra.listener;

import com.example.scheduler.history.infra.persistent.MisfireSkipHistory;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CronMisfireListener extends TriggerListenerSupport {
    private final MisfireSkipHistory history;
    @Override public String getName() { return "cron-misfire-history"; }
    @Override public void triggerMisfired(Trigger trigger) {
        if (trigger instanceof CronTrigger cron && cron.getMisfireInstruction()==CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING)
            history.record(cron, Instant.now());
        // Propagate audit failure to Quartz scheduler-error reporting; this is not the Quartz storage transaction.
    }
}
