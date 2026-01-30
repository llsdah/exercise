package com.example.scheduler.job.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ScheduleInfoId implements Serializable {

    @Column(name = "TENANT_ID", length = 14)
    private String tenantId;

    @Column(name = "SCHEDULE_GROUP", length = 50)
    private String scheduleGroup;

    @Column(name = "SCHEDULE_NAME", length = 200)
    private String scheduleName;
}