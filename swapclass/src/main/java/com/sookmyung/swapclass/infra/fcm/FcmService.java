package com.sookmyung.swapclass.infra.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sookmyung.swapclass.domain.push.repository.PushSubscriptionRepository;
import com.google.firebase.messaging.MessagingErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final PushSubscriptionRepository pushSubscriptionRepository;

    // FCM 토큰으로 푸시 알림 발송
    public void sendPushNotification(String fcmToken, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 푸시 발송 성공: {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("FCM 푸시 발송 실패 - token: {}, error: {}", fcmToken, e.getMessage());
        }
    }

    // 딥링크 포함 푸시 알림 발송
    public void sendPushWithDeepLink(String fcmToken, String title, String body, String deepLink) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("deepLink", deepLink)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 푸시 발송 성공 (딥링크 포함): {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("FCM 푸시 발송 실패 - token: {}, error: {}", fcmToken, e.getMessage());
            // NotRegistered, Device unregistered 에러 시 토큰 삭제
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                pushSubscriptionRepository.findByFcmToken(fcmToken)
                        .ifPresent(pushSubscriptionRepository::delete);
                log.info("만료된 FCM 토큰 삭제: {}", fcmToken);
            }
        }
    }
}
