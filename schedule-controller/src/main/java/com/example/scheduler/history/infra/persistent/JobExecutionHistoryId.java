package com.example.scheduler.history.infra.persistent;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode  // 명시적으로
public class JobExecutionHistoryId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;  // 직렬화 버전 추가

    private String tenantId;
    private String scheduleGroup;
    private String scheduleName;
    private LocalDateTime startTime;
}