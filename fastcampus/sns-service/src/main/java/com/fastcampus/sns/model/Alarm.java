package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.AlarmEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Getter
public class Alarm {

    private Integer id;
    // FIXME user 데이터 불필요
    //private User user;
    private AlarmType alarmType;
    private AlarmArguments alarmArguments;

    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Alarm fromEntity(AlarmEntity entity) {
        log.info("===== CALL from entity");
        return new Alarm(
                entity.getId(),
                // FIXME user 데이터 불필요
                //User.fromEntity(entity.getUser()),
                entity.getAlarmType(),
                entity.getAlarmArguments(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}
