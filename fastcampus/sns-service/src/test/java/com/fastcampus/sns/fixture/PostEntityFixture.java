package com.fastcampus.sns.fixture;

import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;

// 가짜 게시글 반환용
public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId,Integer userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        PostEntity result = new PostEntity();

        result.setUser(user);
        result.setId(postId);

        return result;
    }
}
