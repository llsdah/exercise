package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.UserRole;
import com.fastcampus.sns.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Integer userId;
    private String userName;
    private String password;

    private UserRole userRole;
    private Timestamp registerAt;
    private Timestamp updateAt;
    private Timestamp deleteAt;


    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserRole(),
                user.getRegisterAt(),
                user.getUpdateAt(),
                user.getDeleteAt()
        );
    }

}
