package com.sookmyung.swapclass.domain.push.service;

import com.sookmyung.swapclass.domain.push.dto.request.PushSubscribeRequest;
import com.sookmyung.swapclass.domain.push.entity.PushSubscription;
import com.sookmyung.swapclass.domain.push.repository.PushSubscriptionRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.infra.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    // FCM 토큰 등록
    @Transactional
    public void subscribe(Long userId, PushSubscribeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 등록된 토큰이면 업데이트
        pushSubscriptionRepository.findByUserIdAndFcmToken(userId, request.getFcmToken())
                .ifPresentOrElse(
                        sub -> log.info("이미 등록된 FCM 토큰"),
                        () -> {
                            PushSubscription subscription = PushSubscription.builder()
                                    .user(user)
                                    .fcmToken(request.getFcmToken())
                                    .deviceType(request.getDeviceType())
                                    .build();
                            pushSubscriptionRepository.save(subscription);
                        }
                );
    }

    // FCM 토큰 해제
    @Transactional
    public void unsubscribe(Long userId, String fcmToken) {
        PushSubscription subscription = pushSubscriptionRepository
                .findByUserIdAndFcmToken(userId, fcmToken)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        pushSubscriptionRepository.delete(subscription);
    }

    // 특정 유저에게 푸시 알림 발송
    public void sendToUser(Long userId, String title, String body) {
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(userId);
        for (PushSubscription subscription : subscriptions) {
            fcmService.sendPushNotification(subscription.getFcmToken(), title, body);
        }
    }

    // 딥링크 포함 푸시 알림 발송
    public void sendToUserWithDeepLink(Long userId, String title, String body, String deepLink) {
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(userId);
        for (PushSubscription subscription : subscriptions) {
            fcmService.sendPushWithDeepLink(subscription.getFcmToken(), title, body, deepLink);
        }
    }
}
