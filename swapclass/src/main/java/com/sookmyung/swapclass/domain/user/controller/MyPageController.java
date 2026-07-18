package com.sookmyung.swapclass.domain.user.controller;

import com.sookmyung.swapclass.domain.user.dto.request.NotificationToggleRequest;
import com.sookmyung.swapclass.domain.user.dto.request.PasswordChangeRequest;
import com.sookmyung.swapclass.domain.user.dto.response.MyInfoResponse;
import com.sookmyung.swapclass.domain.user.dto.response.NotificationSettingResponse;
import com.sookmyung.swapclass.domain.user.service.MyPageService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")   // 마이페이지 계정 관리 공통 경로
public class MyPageController {

    private final MyPageService myPageService;

    // [내 정보 조회] JWT 필터가 세팅한 userId를 꺼내 사용
    @GetMapping
    public ApiResponse<MyInfoResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(myPageService.getMyInfo(userId));
    }

    // [알림 수신 토글] 알림 수신 여부 on/off
    @PatchMapping("/notification")
    public ApiResponse<NotificationSettingResponse> updateNotification(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody NotificationToggleRequest request) {
        return ApiResponse.success(
                myPageService.updateNotification(userId, request.notificationEnabled()));
    }

    // [비밀번호 변경] 현재 비밀번호 확인 후 변경
    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        myPageService.changePassword(userId, request);
        return ApiResponse.success(null, "비밀번호가 변경되었습니다.");
    }
}
