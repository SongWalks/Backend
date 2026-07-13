package com.sookmyung.swapclass.domain.post.controller;

import com.sookmyung.swapclass.domain.post.dto.request.PostCreateRequest;
import com.sookmyung.swapclass.domain.post.dto.request.PostUpdateRequest;
import com.sookmyung.swapclass.domain.post.dto.response.MyPostResponse;
import com.sookmyung.swapclass.domain.post.dto.response.PostCreateResponse;
import com.sookmyung.swapclass.domain.post.dto.response.PostDetailResponse;
import com.sookmyung.swapclass.domain.post.dto.response.PostFeedResponse;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.service.PostService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import com.sookmyung.swapclass.global.response.PageResponse;

import java.util.List;
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

    // 게시글 피드 조회 (매칭 전, 최신순, 오프셋 페이징. 학과 필터 선택)
    @GetMapping
    public ApiResponse<PageResponse<PostFeedResponse>> getFeed(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String dept,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(postService.getFeed(userId, dept, page, size));
    }

    // 내 교환 게시글 목록 (status 선택: MATCHABLE / IN_EXCHANGE / COMPLETED, 없으면 전체)
    @GetMapping("/me")
    public ApiResponse<List<MyPostResponse>> getMyPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) PostStatus status
    ) {
        return ApiResponse.success(postService.getMyPosts(userId, status));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.getPost(postId, userId));
    }

    // 게시글 수정 (원하는 과목·오픈채팅 링크)
    @PatchMapping("/{postId}")
    public ApiResponse<Void> updatePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        postService.updatePost(userId, postId, request);
        return ApiResponse.success(null, "게시글이 수정되었습니다.");
    }

    // 게시글 삭제 (soft delete)
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        postService.deletePost(userId, postId);
        return ApiResponse.success(null, "게시글이 삭제되었습니다.");
    }
}
