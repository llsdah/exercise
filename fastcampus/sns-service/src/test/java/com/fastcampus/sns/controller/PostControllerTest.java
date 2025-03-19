package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.PostCommentRequest;
import com.fastcampus.sns.controller.request.PostCreateRequest;
import com.fastcampus.sns.controller.request.PostModifyRequest;
import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @Test
    @WithMockUser
    void 포스트_작성() throws Exception {

        String title = "title";
        String body ="body";
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser // 익명유저로 날린 경우
    void 포스트_작성_미로그인경우() throws Exception {

        String title = "title";
        String body ="body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트_수정() throws Exception {

        String title = "title";
        String body ="body";
        String userName = "userName";
        Integer postId = 1;
        Integer userId = 1;

        // mocking
        when(postService.modify(eq(title),eq(body),any(),any()))
                .thenReturn(Post.fromEntity(PostEntityFixture.get(userName,postId,userId)));


        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser // 익명유저로 날린 경우
    void 포스트_수정_미로그인경우() throws Exception {

        String title = "title";
        String body ="body";

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser // 익명유저로 날린 경우
    void 포스트_수정_작성자가아닌경우() throws Exception {

        String title = "title";
        String body ="body";

        // mocking
        // TODO 
        doThrow(new SNSApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title), eq(body), any(),eq(1));

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser // 익명유저로 날린 경우
    void 포스트_수정하는_글이_없는경우() throws Exception {

        String title = "title";
        String body ="body";

        // mocking
        // TODO 
        doThrow(new SNSApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(title), eq(body), any(),eq(1));
        
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_삭제() throws Exception {
        
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isOk());
    }
    
    
    @Test
    @WithMockUser
    void 포스트_삭제_로그인하지_않은경우() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser
    void 포스트_삭제시_작성자와_요청가자_다른경우() throws Exception {

        // mocking
        doThrow(new SNSApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(),any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    
    @Test
    @WithMockUser
    void 포스트_삭제시_포스트가_없는경우() throws Exception {

        // mocking
        doThrow(new SNSApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(),any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 피드목록() throws Exception {

        // TODO : mocking
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 피드목록요청시_로그인하지_않은경우() throws Exception {

        when(postService.my(any(),any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser
    void 좋아요기능() throws Exception {

        // 변화니까 post 요청
        mockMvc.perform(post("/api/v1/posts/1/likes")
                                .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser
    void 좋아요요청시_로그인하지_않은경우() throws Exception {

        mockMvc.perform(post("/api/v1/posts/1/likes")
                                .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요요청시_게시물이_없는경우() throws Exception {

        doThrow(new SNSApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(),any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                                .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser
    void 댓글작성_기능() throws Exception {

        // 변화니까 post 요청
        mockMvc.perform(post("/api/v1/posts/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostCommentRequest("Comment")))

                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 댓글작성시_로그인하지_않은경우() throws Exception {

        mockMvc.perform(post("/api/v1/posts/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 댓글작성시_게시물이_없는경우() throws Exception {

        doThrow(new SNSApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).comment(any(),any(), any());
        mockMvc.perform(post("/api/v1/posts/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostCommentRequest("Comment")))
                        // TODO : add request body
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

}
