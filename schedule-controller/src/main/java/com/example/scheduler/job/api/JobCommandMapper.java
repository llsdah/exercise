package com.example.scheduler.job.api;


import com.example.scheduler.job.api.dto.ScheduleRequest;
import com.example.scheduler.job.application.model.JobCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
/**
 * [Translator]
 * Web 요청(ScheduleRequest)을 App 명령(JobCommand)으로 번역하는 책임.
 * "정책 판단", "기본값 설정", "포맷 변환"을 여기서 수행합니다.
 */
@Component
@RequiredArgsConstructor
public class JobCommandMapper {

    public JobCommand toCommand(ScheduleRequest req) {
        // 1. [정책 판단] CRON 타입일 때만 Cron 식 생성, ONCE면 null

        //String cron = req.cronExpression();
        //if (req.trigger() != null && "CRON".equalsIgnoreCase(req.trigger().type()) ) {
        //    cron = buildCronExpression(req.trigger());
        //}

        // 2. [핵심] 파라미터 변환 로직 (Map -> String)
        // args 먼저 넣고, 나머지는 --key=value 형태로 뒤에 붙임
        String flatParameters = buildFlatParameters(req.parameters());

        // 3. [기본값 설정] 그룹이 없으면 DEFAULT로 설정
        String group = (req.scheduleGroup() == null || req.scheduleGroup().isBlank()) 
                       ? "default"
                       : req.scheduleGroup();

        // 입력 없으면 "SYSTEM"
        String user = (req.scheduleUser() == null || req.scheduleUser().isBlank())
                ? "system" : req.scheduleUser();

        return new JobCommand(
            group.toLowerCase(),
            req.scheduleName().toLowerCase(),
            req.scheduleType().toUpperCase(),
            req.jobType().toUpperCase(),
            req.scheduleUser(),
            req.cronExpression(),
            req.command(),
            flatParameters,
            req.startTime(),
            req.endTime()
        );
    }

    /**
     * 파라미터 Map을 실행 가능한 CLI 문자열로 변환
     * 규칙 1: "args" 키가 있으면 그 안의 리스트를 순서대로 맨 앞에 배치
     * 규칙 2: 그 외의 키들은 --key=value 형태로 뒤에 배치
     */
    private String buildFlatParameters(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        List<String> commandParts = new ArrayList<>();

        // 1. "args" 처리 (위치 인자, 순서 보장)
        if (parameters.containsKey("args")) {
            Object argsObj = parameters.get("args");
            if (argsObj instanceof List<?>) {
                List<?> list = (List<?>) argsObj;
                for (Object item : list) {
                    commandParts.add(String.valueOf(item));
                }
            }
        }

        // 2. 그 외 Key-Value 처리 (옵션 인자)
        // LinkedHashMap인 경우 입력 순서 유지됨 (Jackson 기본)
        parameters.forEach((key, value) -> {
            if (!"args".equals(key)) {
                // 예: --timeout=30 형태로 변환 (CLI 표준 스타일)
                // 필요하다면 포맷 변경 가능 (예: "-" + key + " " + value)
                commandParts.add("--" + key + "=" + value);
            }
        });

        // 공백으로 연결하여 반환
        return String.join(" ", commandParts);
    }
}