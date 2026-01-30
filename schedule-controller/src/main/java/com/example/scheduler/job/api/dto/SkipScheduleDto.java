package com.example.scheduler.job.api.dto;

import com.example.scheduler.job.infra.persistent.JobSkipEntity;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SkipScheduleDto(
        Long id,              // 스킵 취소(삭제)를 위해 필요한 식별자
        String scheduleGroup,
        String scheduleName,

        // [변경] 단일 값(LocalDateTime) -> 리스트(List)
        // 5개의 슬롯 중 유효한 시간만 모아서 내려줍니다.
        List<LocalDateTime> skipTimes
) {
    // Entity -> DTO 변환 편의 메서드
    public static SkipScheduleDto from(JobSkipEntity entity) {
        return SkipScheduleDto.builder()
                .id(entity.getId())
                .scheduleGroup(entity.getJobGroup())
                .scheduleName(entity.getJobName())
                // [핵심] Entity의 5개 컬럼을 하나의 리스트로 변환해서 매핑
                // Entity에 만들어둔 getActiveSkipTimes() 메서드 활용
                .skipTimes(entity.getActiveSkipTimes())
                .build();
    }
}