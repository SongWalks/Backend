package com.sookmyung.swapclass.domain.lounge.controller;

import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostListResponse;
import com.sookmyung.swapclass.domain.lounge.service.MyLoungeService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")   // 마이페이지의 라운지 목록 조회
public class MyLoungeController {

    private final MyLoungeService myLoungeService;

    // [내 라운지 게시글 목록] 최신순
    @GetMapping("/lounge-posts")
    public ApiResponse<LoungePostListResponse> getMyLoungePosts(
            @AuthenticationPrincipal Long userId) {
        return ApiResponse.success(myLoungeService.getMyPosts(userId));
    }

    // [북마크한 라운지 게시글 목록] 최신순
    @GetMapping("/lounge-bookmarks")
    public ApiResponse<LoungePostListResponse> getMyLoungeBookmarks(
            @AuthenticationPrincipal Long userId) {
        return ApiResponse.success(myLoungeService.getMyBookmarks(userId));
    }
}
