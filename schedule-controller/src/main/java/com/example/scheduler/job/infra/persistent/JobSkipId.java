package com.example.scheduler.job.infra.persistent;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSkipId implements Serializable {
    private String tenantId;
    private String scheduleGroup;
    private String scheduleName;
    private LocalDateTime skipTime;
}