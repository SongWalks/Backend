package com.sookmyung.swapclass.infra.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

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
        }
    }
}
