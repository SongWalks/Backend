package com.sookmyung.swapclass.domain.home.controller;

import com.sookmyung.swapclass.domain.home.dto.response.HomeResponse;
import com.sookmyung.swapclass.domain.home.service.HomeService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    // 홈화면 통합 조회 (인증 optional: 비로그인이면 userId == null)
    @GetMapping("/api/home")
    public ApiResponse<HomeResponse> getHome(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(homeService.getHome(userId, page, size));
    }
}
