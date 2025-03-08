package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;


    @Transactional
    public void create(String title, String body, String username){

        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(username).orElseThrow(
                () -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES, String.format("%s user not found",username))
        );
        // user save
        postEntityRepository.save(PostEntity.of(title,body,userEntity));

    }

    public Post modify(String title, String body, String username, Integer postId) {
        // user가 없는 경우
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(username).orElseThrow(
                () -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES, String.format("%s user not found",username))
        );

        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SNSApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded",postId)));

        // post permission

        if (postEntity.getUser() != userEntity) {
            throw new SNSApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s , permission denied",username));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        // 업데이트까지 동시에 진행합니다.
        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String username, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(username).orElseThrow(
                () -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES, String.format("%s user not found",username))
        );

        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SNSApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded",postId)));

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SNSApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s , permission denied",username));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES, String.format("%s not founded", userName)));


        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }


}
