package com.example.scheduler.job.infra.persistent;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table(name = "job_skip_log",
        uniqueConstraints = @UniqueConstraint(columnNames = {"scheduleGroup", "scheduleName"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobSkipEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobGroup;

    @Column(nullable = false)
    private String jobName;

    // [핵심] 5개의 고정 슬롯 (컬럼 5개로 제한)
    private LocalDateTime skipTime1;
    private LocalDateTime skipTime2;
    private LocalDateTime skipTime3;
    private LocalDateTime skipTime4;
    private LocalDateTime skipTime5;

    /**
     * [로직 구현] 스킵 시간 등록 (슬롯 알고리즘)
     * 1. 빈 자리(null)가 있으면 넣는다.
     * 2. 빈 자리가 없으면, '과거 시간(이미 지난 것)'을 찾아서 덮어쓴다.
     * 3. 그것도 없으면(모두 미래 시간으로 꽉 참) -> 실패 반환
     */
    public boolean addSkipTime(LocalDateTime newTime, LocalDateTime now) {

        // 이미 해당 시간이 슬롯에 존재한다면, 또 넣을 필요가 없음 -> 성공으로 간주하고 종료
        if (Objects.equals(skipTime1, newTime)) return true;
        if (Objects.equals(skipTime2, newTime)) return true;
        if (Objects.equals(skipTime3, newTime)) return true;
        if (Objects.equals(skipTime4, newTime)) return true;
        if (Objects.equals(skipTime5, newTime)) return true;

        // 빈 슬롯 찾기
        if (skipTime1 == null) { skipTime1 = newTime; return true; }
        if (skipTime2 == null) { skipTime2 = newTime; return true; }
        if (skipTime3 == null) { skipTime3 = newTime; return true; }
        if (skipTime4 == null) { skipTime4 = newTime; return true; }
        if (skipTime5 == null) { skipTime5 = newTime; return true; }

        // 과거 데이터(Garbage) 찾아서 덮어쓰기 (Recycle)
        if (skipTime1.isBefore(now)) { skipTime1 = newTime; return true; }
        if (skipTime2.isBefore(now)) { skipTime2 = newTime; return true; }
        if (skipTime3.isBefore(now)) { skipTime3 = newTime; return true; }
        if (skipTime4.isBefore(now)) { skipTime4 = newTime; return true; }
        if (skipTime5.isBefore(now)) { skipTime5 = newTime; return true; }

        // 3. 꽉 찼고, 모두 미래 데이터임 -> 등록 불가
        return false;
    }

    /**
     * [취소 로직] 특정 시간의 스킵 예약을 취소함
     * - 슬롯 1~5를 뒤져서 해당 시간이 있으면 null로 초기화
     */
    public boolean removeSkipTime(LocalDateTime targetTime) {
        boolean removed = false;

        // Objects.equals는 null 안전하게 비교해줍니다.
        if (Objects.equals(skipTime1, targetTime)) { skipTime1 = null; removed = true; }
        else if (Objects.equals(skipTime2, targetTime)) { skipTime2 = null; removed = true; }
        else if (Objects.equals(skipTime3, targetTime)) { skipTime3 = null; removed = true; }
        else if (Objects.equals(skipTime4, targetTime)) { skipTime4 = null; removed = true; }
        else if (Objects.equals(skipTime5, targetTime)) { skipTime5 = null; removed = true; }

        return removed; // 삭제된 게 있으면 true 반환
    }

    /**
     * [로직 구현] 스킵 실행 처리 (Consume)
     * 해당 시간이 있으면 null로 비워줌 (슬롯 확보)
     */
    public boolean consumeTime(LocalDateTime targetTime) {
        boolean matched = false;
        if (Objects.equals(skipTime1, targetTime)) { skipTime1 = null; matched = true; }
        else if (Objects.equals(skipTime2, targetTime)) { skipTime2 = null; matched = true; }
        else if (Objects.equals(skipTime3, targetTime)) { skipTime3 = null; matched = true; }
        else if (Objects.equals(skipTime4, targetTime)) { skipTime4 = null; matched = true; }
        else if (Objects.equals(skipTime5, targetTime)) { skipTime5 = null; matched = true; }

        return matched;
    }

    // 조회용: null 빼고 리스트로 반환
    public List<LocalDateTime> getActiveSkipTimes() {
        return Stream.of(skipTime1, skipTime2, skipTime3, skipTime4, skipTime5)
                .filter(Objects::nonNull)
                .sorted()
                .toList(); // Java 16+
    }
}