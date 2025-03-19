package com.fastcampus.sns.model;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmArguments {
    private Integer fromUserId; // 알람 발생시킨사람
    private Integer targetId; // 알람 주체
    //private Integer fromPostId; // 알람 발생시킨사람

    //List<Integer> targetIds;
}
