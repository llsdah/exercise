package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.model.AlarmArguments;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.Comment;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.*;
import com.fastcampus.sns.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepositoty alarmEntityRepositoty;

    @Transactional
    public void create(String title, String body, String username){

        // user find
        UserEntity userEntity = getUserEntityOrException(username);

        // user save
        postEntityRepository.save(PostEntity.of(title,body,userEntity));

    }

    public Post modify(String title, String body, String username, Integer postId) {
        // user가 없는 경우
        // user find
        UserEntity userEntity = getUserEntityOrException(username);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

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
        UserEntity userEntity = getUserEntityOrException(username);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

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
        UserEntity userEntity = getUserEntityOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = getUserEntityOrException(userName);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // check liked => exception
        likeEntityRepository.findByUserAndPost(userEntity,postEntity).ifPresent(it -> {
            throw new SNSApplicationException(ErrorCode.ALREADY_LIKES, String.format("%s already likes %s", userEntity.getUserName(), postEntity.getTitle()));
        });

        likeEntityRepository.save(LikeEntity.of(userEntity,postEntity));

        alarmEntityRepositoty.save(AlarmEntity.of(
                postEntity.getUser(),AlarmType.NEW_LIKE_ON_POST, new AlarmArguments(userEntity.getId(), postEntity.getId())));

    }

    public Integer likeCount(Integer postId) {
        // post exist

        PostEntity postEntity = getPostEntityOrException(postId);

        //List<LikeEntity> list = likeEntityRepository.findAllByPost(postEntity);
        //return list.size();
        Integer result = likeEntityRepository.countByPost(postEntity);

        return result;
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment){
        UserEntity userEntity = getUserEntityOrException(userName);
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        //comment save
        commentEntityRepository.save(CommentEntity.of(userEntity,postEntity,comment));

        alarmEntityRepositoty.save(AlarmEntity.of(
                postEntity.getUser(),AlarmType.NEW_COMMENT_ON_POST, new AlarmArguments(userEntity.getId(), postEntity.getId())));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity post = getPostEntityOrException(postId);
        return commentEntityRepository.findByPost(post, pageable).map(Comment::fromEntity);
    }

    private PostEntity getPostEntityOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SNSApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded",postId)));
    }

    private UserEntity getUserEntityOrException(String username) {
        return userEntityRepository.findByUserName(username).orElseThrow(() ->
                new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES, String.format("%s not founded",username)));
    }
}
