package com.sookmyung.swapclass.domain.post.controller;

import com.sookmyung.swapclass.domain.post.dto.request.PostCreateRequest;
import com.sookmyung.swapclass.domain.post.dto.response.PostCreateResponse;
import com.sookmyung.swapclass.domain.post.dto.response.PostDetailResponse;
import com.sookmyung.swapclass.domain.post.service.PostService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostCreateResponse> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostCreateResponse response = postService.createPost(userId, request);
        return ApiResponse.success(response, "게시글이 등록되었습니다.");
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.getPost(postId, userId));
    }
}
