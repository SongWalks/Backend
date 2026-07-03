package com.sookmyung.swapclass.domain.notification.service;

import com.sookmyung.swapclass.domain.notification.entity.Notification;
import com.sookmyung.swapclass.domain.notification.entity.NotificationType;
import com.sookmyung.swapclass.domain.notification.repository.NotificationRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 목록 조회
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 읽지 않은 알림 개수 조회
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // 단건 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notification.markAsRead();
    }

    // 전체 읽음 처리
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    // 알림 생성 공통 메서드
    @Transactional
    public void createNotification(User user, NotificationType type, String title, String body, Long relatedId) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .relatedId(relatedId)
                .build();
        notificationRepository.save(notification);

    }

    // 교환 요청 도착 알림
    @Transactional
    public void sendProposalArrivedNotification(User receiver, Long proposalId) {
        createNotification(
                receiver,
                NotificationType.MATCH_PROPOSAL,
                "새 교환 요청",
                "교환 요청이 도착했습니다! 30분 내로 확인해주세요.",
                proposalId
        );
    }

    // 매칭 무산 롤백 알림
    @Transactional
    public void sendMatchRollbackNotification(User user) {
        createNotification(
                user,
                NotificationType.CANCEL,
                "교환 매칭 무산",
                "매칭이 무산되어 다시 교환을 요청할 수 있는 상태로 돌아갔습니다. 다시 요청해주세요!",
                null
        );
    }

    // 교환 시간 30분 전 알림
    @Transactional
    public void sendExchangeSoon30MinNotification(User user, Long roomId) {
        createNotification(
                user,
                NotificationType.VERIFY_SOON,
                "교환 시간 30분 전",
                "교환 시간 30분 전입니다! 미리 준비해주세요.",
                roomId
        );
    }

    // 교환 시간 10분 전 알림
    @Transactional
    public void sendExchangeSoon10MinNotification(User user, Long roomId) {
        createNotification(
                user,
                NotificationType.VERIFY_SOON,
                "교환 시간 10분 전",
                "교환 시간 10분 전입니다! 곧 인증이 시작됩니다.",
                roomId
        );
    }

    // 교환 시간 5분 전 알림 (인증 시작)
    @Transactional
    public void sendVerifyStartNotification(User user, Long roomId) {
        createNotification(
                user,
                NotificationType.VERIFY_START,
                "과목 보유 인증 시작",
                "교환 시간 5분 전입니다! 지금 과목 보유 인증을 진행해주세요.",
                roomId
        );
    }
}