package com.sookmyung.swapclass.domain.post.controller;

import com.sookmyung.swapclass.domain.post.dto.response.PostLikeItemResponse;
import com.sookmyung.swapclass.domain.post.service.PostLikeService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MyLikeController {

    private final PostLikeService postLikeService;

    // 내 찜 목록 조회 (최신순)
    @GetMapping("/likes")
    public ApiResponse<List<PostLikeItemResponse>> getMyLikes(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(postLikeService.getMyLikes(userId));
    }
}
