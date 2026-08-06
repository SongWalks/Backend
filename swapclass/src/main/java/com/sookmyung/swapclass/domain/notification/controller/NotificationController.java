package com.sookmyung.swapclass.domain.notification.controller;

import com.sookmyung.swapclass.domain.notification.entity.Notification;
import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            @AuthenticationPrincipal Long userId) {
        List<Map<String, Object>> notifications = notificationService.getNotifications(userId)
                .stream()
                .map(n -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", n.getId());
                    map.put("type", n.getType());
                    map.put("title", n.getTitle() != null ? n.getTitle() : "");
                    map.put("body", n.getBody() != null ? n.getBody() : "");
                    map.put("deepLink", n.getDeepLink() != null ? n.getDeepLink() : "");
                    map.put("relatedId", n.getRelatedId() != null ? n.getRelatedId() : 0L);
                    map.put("isRead", n.isRead());
                    map.put("createdAt", n.getCreatedAt());
                    return map;
                })
                .toList();
        long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        )));
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal Long userId) {
        long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", unreadCount)));
    }

    // 단건 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "알림을 읽었습니다."));
    }

    // 전체 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "모든 알림을 읽었습니다."));
    }
}
