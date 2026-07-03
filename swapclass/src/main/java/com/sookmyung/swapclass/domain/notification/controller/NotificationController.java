package com.sookmyung.swapclass.domain.notification.controller;

import com.sookmyung.swapclass.domain.notification.entity.Notification;
import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 임시 userId — JWT 구현 후 SecurityContext에서 꺼내는 방식으로 교체 예정
    private static final Long TEMP_USER_ID = 1L;

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications() {
        List<Notification> notifications = notificationService.getNotifications(TEMP_USER_ID);
        long unreadCount = notificationService.getUnreadCount(TEMP_USER_ID);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        )));
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        long unreadCount = notificationService.getUnreadCount(TEMP_USER_ID);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "unreadCount", unreadCount
        )));
    }

    // 단건 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId, TEMP_USER_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "알림을 읽었습니다."));
    }

    // 전체 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead(TEMP_USER_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "모든 알림을 읽었습니다."));
    }
}