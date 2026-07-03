package com.sookmyung.swapclass.domain.notification.repository;

import com.sookmyung.swapclass.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 목록 조회 (최신순)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 읽지 않은 알림 개수
    long countByUserIdAndIsReadFalse(Long userId);

    // 전체 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}