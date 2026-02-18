package com.example.scheduler.job.infra.persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode // [필수] 복합키는 equals, hashCode 구현 필수
public class JobEntityId implements Serializable {

    @Column(name = "TENANT_ID", length = 14, nullable = false)
    private String tenantId;

    @Column(name = "SCHEDULE_GROUP", length = 50, nullable = false)
    private String scheduleGroup;

    @Column(name = "SCHEDULE_NAME", length = 200, nullable = false)
    private String scheduleName;
}