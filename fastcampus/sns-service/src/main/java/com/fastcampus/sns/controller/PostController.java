package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.PostCreateRequest;
import com.fastcampus.sns.controller.response.PostResponse;
import com.fastcampus.sns.controller.response.Response;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    public final PostService postService;

    @PostMapping
    @ResponseBody
    public Response<Void> create (@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(),request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    @ResponseBody
    public Response<PostResponse> modify (@PathVariable Integer postId, @RequestBody PostCreateRequest request, Authentication authentication) {
        log.debug("modify {}",postId);
        Post post = postService.modify(request.getTitle(),request.getBody(), authentication.getName(),postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    @ResponseBody
    public Response<Void> delete (@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    @ResponseBody
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    // 페이징 수행시 호출 예시 */api/v1/posts/my?size=2&page=0
    @GetMapping("/my")
    @ResponseBody
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication) {
        return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
    }


}
