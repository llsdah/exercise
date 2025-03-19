package com.fastcampus.sns.model.entity;

import com.fastcampus.sns.model.AlarmArguments;
import com.fastcampus.sns.model.AlarmType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"alarm\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at is NULL")
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 알람을 받을 사람.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 알람의 타입 - 게시글, 좋아요 등등
    // 알람은 누구외 좋아요 눌렀다 등의 내용을 묶어서 표시
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    // 데이터 클래스에 넣으려면? -> json 타입으로 수행
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private AlarmArguments alarmArguments;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    // PostEntity 생성하는 메소드
    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArguments alarmArguments) {
        AlarmEntity entity = new AlarmEntity();

        entity.setUser(userEntity);
        entity.setAlarmType(alarmType);
        entity.setAlarmArguments(alarmArguments);

        return entity;
    }
}
