package com.fastcampus.sns.service;


import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockitoBean
    private PostEntityRepository postEntityRepository;

    @MockitoBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 포스트작성_작성_정상() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when( userEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title,body,userName) );

    }

    @Test
    void 포스트작성_작성_사용자미존재경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when( userEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> postService.create(title,body,userName) );

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUNDES, e.getErrorCode());

    }


    @Test
    void 포스트수정_정상() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;

        // mocking
        PostEntity mockPostEntity = PostEntityFixture.get(userName,postId, userId);
        UserEntity mockUserEntity = mockPostEntity.getUser();

        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mockUserEntity));
        when( postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPostEntity));
        when( postEntityRepository.saveAndFlush(any())).thenReturn(mockPostEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title,body,userName,postId) );

    }
    
    @Test
    void 포스트수정_포스트가_없는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;

        // mocking
        PostEntity mockPostEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity mockUserEntity = mockPostEntity.getUser();

        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mockUserEntity));
        when( postEntityRepository.findById(postId)).thenReturn(Optional.empty());
        
        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> postService.modify(title,body,userName,postId) );

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }
    
    @Test
    void 포스트수정_권한이_없는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;
        // mocking
        PostEntity mockPostEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity mockUserEntity = mockPostEntity.getUser();
        UserEntity writer= UserEntityFixture.get("writer","writer",1);

        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when( postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPostEntity));
        
        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> postService.modify(title,body,userName,postId) );

        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    
    @Test
    void 포스트삭제_정상() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;

        // mocking
        PostEntity mockPostEntity = PostEntityFixture.get(userName,postId, userId);
        UserEntity mockUserEntity = mockPostEntity.getUser();

        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mockUserEntity));
        when( postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPostEntity));

        Assertions.assertDoesNotThrow(() -> postService.delete(userName,postId) );

    }



    @Test
    void 포스트삭제_포스트가_없는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;

        // mocking
        PostEntity mockPostEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity mockUserEntity = mockPostEntity.getUser();

        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mockUserEntity));
        when( postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> postService.delete(userName,postId) );

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }
    
    @Test
    void 포스트삭제_권한이_없는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;
        // mocking
        PostEntity mockPostEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity writer= UserEntityFixture.get("writer","writer",1);

        when( userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when( postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPostEntity));

        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> postService.delete(userName,postId) );

        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 피드목록_요청성공() {

        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내피드목록_요청성공() {

        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUser(any(), pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.my("",pageable));
    }



}
