package com.sookmyung.swapclass.domain.match.controller;

import com.sookmyung.swapclass.domain.match.dto.response.RecommendationResponse;
import com.sookmyung.swapclass.domain.match.service.MatchService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // 추천 매칭 목록 (postId 선택: 특정 내 게시글 기준 / 없으면 내 전체 게시글 기준)
    @GetMapping("/recommendations")
    public ApiResponse<RecommendationResponse> getRecommendations(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(matchService.getRecommendations(userId, postId, page, size));
    }
}
