package com.fastcampus.sns.controller.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserJoinRequest {

    private String name;
    private String password;

}
