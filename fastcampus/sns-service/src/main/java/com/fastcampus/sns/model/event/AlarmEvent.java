package com.fastcampus.sns.model.event;

import com.fastcampus.sns.model.AlarmArguments;
import com.fastcampus.sns.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {

    private Integer receiverUserId;
    private AlarmType alarmType;
    private AlarmArguments args;

}
