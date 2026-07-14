package com.sookmyung.swapclass.domain.post.controller;

import com.sookmyung.swapclass.domain.post.dto.response.PostLikeResponse;
import com.sookmyung.swapclass.domain.post.service.PostLikeService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 게시글 찜하기
    @PostMapping("/{postId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostLikeResponse> like(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postLikeService.addLike(userId, postId), "게시글을 찜했습니다.");
    }

    // 게시글 찜 취소
    @DeleteMapping("/{postId}/likes")
    public ApiResponse<PostLikeResponse> unlike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postLikeService.removeLike(userId, postId), "찜을 해제했습니다.");
    }
}
