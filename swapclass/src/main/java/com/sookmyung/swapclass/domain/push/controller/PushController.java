package com.sookmyung.swapclass.domain.push.controller;

import com.sookmyung.swapclass.domain.push.dto.request.PushSubscribeRequest;
import com.sookmyung.swapclass.domain.push.service.PushService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/subscriptions")
@RequiredArgsConstructor
public class PushController {

    private final PushService pushService;

    // FCM 토큰 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> subscribe(
            @Valid @RequestBody PushSubscribeRequest request,
            @AuthenticationPrincipal Long userId) {
        pushService.subscribe(userId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(null, "푸시 알림이 등록되었습니다."));
    }

    // FCM 토큰 해제
    @DeleteMapping("/{fcmToken}")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @PathVariable String fcmToken,
            @AuthenticationPrincipal Long userId) {
        pushService.unsubscribe(userId, fcmToken);
        return ResponseEntity.ok(ApiResponse.success(null, "푸시 알림 구독이 해제되었습니다."));
    }
}
