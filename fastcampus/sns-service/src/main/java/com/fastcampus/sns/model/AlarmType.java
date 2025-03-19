package com.fastcampus.sns.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 알람 발생시킨 사람, 발생시킨 주체(post, comment 등)
@RequiredArgsConstructor
@Getter
public enum AlarmType {
    NEW_COMMENT_ON_POST("new comment"),
    NEW_LIKE_ON_POST("new like"),
    ;

    private final String alarmText;
}
