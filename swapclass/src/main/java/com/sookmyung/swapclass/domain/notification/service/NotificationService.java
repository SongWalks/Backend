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

    // ─── 기본 CRUD ────────────────────────────────────────────

    // 알림 목록 조회 (최신순)
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

    // ─── 공통 알림 생성 ───────────────────────────────────────

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

    // ─── 매칭 및 거래 알림 ────────────────────────────────────

    // 자동 매칭 제안 알림
    @Transactional
    public void sendAutoMatchProposalNotification(User user, String courseName, int priority, Long postId) {
        createNotification(
            user,
            NotificationType.MATCH_PROPOSAL,
            "매칭 제안",
            courseName + "의 " + priority + "순위 과목을 찾았습니다! 교환을 제안해보세요!",
            postId
        );
    }

    // 교환 요청 수신 알림
    @Transactional
    public void sendProposalReceivedNotification(User receiver, Long proposalId) {
        createNotification(
            receiver,
            NotificationType.MATCH_REQUESTED,
            "새 교환 요청",
            "✨ 누군가 회원님의 게시글에 교환을 요청했습니다! (30분 내 확인 필요)",
            proposalId
        );
    }

    // 매칭 수락 알림
    @Transactional
    public void sendMatchAcceptedNotification(User user, Long chatRoomId) {
        createNotification(
            user,
            NotificationType.MATCH_ACCEPTED,
            "매칭 성사",
            "🤝 매칭이 성사되었습니다! 교환 준비방으로 이동하여 시간을 조율해 주세요.",
            chatRoomId
        );
    }

    // 매칭 거절 알림
    @Transactional
    public void sendMatchRejectedNotification(User user, Long postId) {
        createNotification(
            user,
            NotificationType.MATCH_REJECTED,
            "교환 요청 거절",
            "상대방이 다른 거래를 시작했습니다🥺. 더 좋은 제안을 할 수 있을거에요.",
            postId
        );
    }

    // 매칭 타임아웃 무산 알림
    @Transactional
    public void sendMatchTimeoutNotification(User user) {
        createNotification(
            user,
            NotificationType.MATCH_TIMEOUT,
            "매칭 무산",
            "매칭이 무산되어 다시 교환을 요청할 수 있는 상태로 돌아갔습니다. 다시 요청해주세요!",
            null
        );
    }

    // 매칭 무산 롤백 알림 (거래 파기)
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

    // ─── 교환 일정 알림 ───────────────────────────────────────

    // 교환 시간 확정 알림
    @Transactional
    public void sendExchangeScheduledNotification(User user, String scheduledTime, Long chatRoomId) {
        createNotification(
            user,
            NotificationType.EXCHANGE_SCHEDULED,
            "교환 시간 확정",
            scheduledTime + "으로 교환 시간이 확정되었습니다.",
            chatRoomId
        );
    }

    // 교환 30분 전 알림
    @Transactional
    public void sendExchangeAlarm30MinNotification(User user, Long chatRoomId) {
        createNotification(
            user,
            NotificationType.VERIFY_30MIN,
            "교환 시간 30분 전",
            "교환까지 30분 남았습니다. 약속 시간을 다시 한번 확인해 주세요.",
            chatRoomId
        );
    }

    // 교환 10분 전 알림
    @Transactional
    public void sendExchangeAlarm10MinNotification(User user, Long chatRoomId) {
        createNotification(
            user,
            NotificationType.VERIFY_10MIN,
            "교환 시간 10분 전",
            "교환까지 10분 남았습니다. 수강신청 화면 캡처본 업로드를 준비해 주세요.",
            chatRoomId
        );
    }

    // 교환 5분 전 알림 (인증 시작)
    @Transactional
    public void sendVerifyStartNotification(User user, Long chatRoomId) {
        createNotification(
            user,
            NotificationType.VERIFY_5MIN,
            "과목 보유 인증 시작",
            "교환 시간 5분 전입니다! 지금 과목 보유 인증을 진행해 주세요.",
            chatRoomId
        );
    }

    // ─── 기타 알림 ────────────────────────────────────────────

    // 찜 알림
    @Transactional
    public void sendLikeNotification(User postOwner, String courseName, Long likerUserId) {
        createNotification(
            postOwner,
            NotificationType.LIKE,
            "게시글 찜",
            "👀 누군가 회원님의 [" + courseName + "] 과목을 찜했습니다! 상대방이 버리는 과목을 확인해 볼까요?",
            likerUserId
        );
    }
}
