package com.example.scheduler.job.api;

import com.example.scheduler.job.api.dto.ScheduleRequest;
import com.example.scheduler.job.application.model.JobCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * [Request Mapper]
 * Web Layer(Controller)의 요청 객체(Request)를 Application Layer의 명령 객체(Command)로 변환합니다.
 * - 역할 1: 입력값의 전처리 (대소문자 통일, 기본값 주입)
 * - 역할 2: 복잡한 데이터 구조 단순화 (Map -> String/JSON 변환)
 * - 역할 3: 테넌트 컨텍스트 주입
 */
@Component
@RequiredArgsConstructor
public class JobCommandMapper {

    public JobCommand toCommand(ScheduleRequest req) {

        // 1. [기본값 정책 및 소문자 변환]
        // tenantId, scheduleGroup, scheduleName은 시스템 내부 식별자로 사용되므로 무조건 소문자로 변환.
        String tenantId = Optional.ofNullable(req.tenantId()).map(String::toLowerCase).orElse(null);

        String jobGroup = StringUtils.hasText(req.scheduleGroup())
                ? req.scheduleGroup().toLowerCase()
                : "default"; // 기본값도 소문자로 유지

        String jobName = StringUtils.hasText(req.scheduleName())
                ? req.scheduleName().toLowerCase()
                : null;

        // 2. [타입 대문자 변환]
        // scheduleType, jobType 등 Enum 매핑 및 규격 통일을 위해 대문자로 변환.
        String scheduleType = Optional.ofNullable(req.scheduleType()).map(String::toUpperCase).orElse(null);
        String jobType = Optional.ofNullable(req.jobType()).map(String::toUpperCase).orElse(null);

        // 3. [파라미터 변환] Map -> CLI String (--key=value)
        String flatParameters = buildFlatParameters(req.parameters());

        // 4. [객체 생성] 변환된 값들을 Command 객체에 매핑
        return new JobCommand(
                tenantId,                       // [소문자 변환 완료]
                jobGroup,                          // [소문자 변환 완료]
                jobName,                        // [소문자 변환 완료]

                scheduleType,                   // [대문자 변환 완료]
                jobType,                        // [대문자 변환 완료]
                req.description(),

                req.cronExpression(),
                req.command(),
                flatParameters,

                req.scheduleStartTime(),
                req.scheduleEndTime(),

                req.regUserId()
        );
    }

    /**
     * [CLI 스타일 파라미터 빌더]
     * Map<String, Object> params -> "arg1 arg2 --option1=value1 --option2=value2"
     */
    private String buildFlatParameters(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        List<String> commandParts = new ArrayList<>();

        if (parameters.containsKey("args")) {
            Object argsObj = parameters.get("args");
            if (argsObj instanceof List<?>) {
                for (Object item : (List<?>) argsObj) {
                    if (item != null) {
                        commandParts.add(String.valueOf(item));
                    }
                }
            }
        }

        parameters.forEach((key, value) -> {
            if (!"args".equals(key) && value != null) {
                commandParts.add("--" + key + "=" + value);
            }
        });

        return String.join(" ", commandParts);
    }
}