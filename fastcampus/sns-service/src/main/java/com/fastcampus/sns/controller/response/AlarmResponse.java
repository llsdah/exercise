package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Data
@AllArgsConstructor
public class AlarmResponse {
    private Integer id;
    // private User user; // 필요없는 정보
    private AlarmType alarmType;
    private AlarmArguments alarmArguments;

    private String text;

    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                // alarm.getUser(),
                alarm.getAlarmType(),
                alarm.getAlarmArguments(),
                alarm.getAlarmType().getAlarmText(),
                alarm.getRegisteredAt(),
                alarm.getUpdatedAt(),
                alarm.getDeletedAt()
        );
    }

}
