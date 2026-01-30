package com.example.scheduler.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    INTERNAL_SERVER_ERROR("E500", "error.server.internal"),
    INVALID_INPUT_VALUE("E400", "error.input.invalid"),

    JOB_NOT_FOUND("J001", "error.job.not.found"),
    SKIP_SLOT_FULL("J002", "error.skip.slot.full"),
    SKIP_NOT_FOUND("J003", "error.skip.not.found"),

    // (참고) S001 코드가 SELECT_SUCCESS와 중복되는데, 의도하신 것이 맞다면 그대로 두셔도 됩니다.
    // 보통 에러는 E나 J 접두어를 쓰므로 수정하시는 것을 추천합니다 (예: S999 or J004)
    JOB_ALREADY_STOPPED("S001", "error.job.already.stopped");

    private final String code;
    private final String messageKey;

    @Override
    public String getMessage() {
        return messageKey;
    }
}
