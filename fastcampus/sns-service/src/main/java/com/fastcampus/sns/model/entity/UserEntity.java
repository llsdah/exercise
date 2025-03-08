package com.fastcampus.sns.model.entity;

import com.fastcampus.sns.model.UserRole;
import jakarta.persistence.*;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"user\"") // Postgre sql 에서는 기존에 user 테이블이 있기에 다르게 작성합니다.
@Setter
@Getter
@ToString
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at is NULL")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private UserRole userRole = UserRole.USER;

    @Column(name = "register_at")
    private Timestamp registerAt;

    @Column(name = "update_at")
    private Timestamp updateAt;

    // soft delete = 삭제 요청에 대한 이력을 알 수 있다.
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registerAt() {
        this.registerAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updateAt() {
        this.updateAt = Timestamp.from(Instant.now());
    }

    // User는 그냥 dto,  User와 UserEntity
    // DB 변경이 아니라 단순 클래스 이름변경이 필요시 JPA의 특징으로 별도 DTO 생성관리
    public static UserEntity of(String userName, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setPassword(password);
        return userEntity;
    }


}
