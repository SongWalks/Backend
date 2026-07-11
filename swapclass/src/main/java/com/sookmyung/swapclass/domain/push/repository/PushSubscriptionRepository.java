package com.sookmyung.swapclass.domain.push.repository;

import com.sookmyung.swapclass.domain.push.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    // 유저의 FCM 토큰 목록 조회
    List<PushSubscription> findByUserId(Long userId);

    // 특정 토큰 조회
    Optional<PushSubscription> findByUserIdAndFcmToken(Long userId, String fcmToken);

    // 특정 토큰 존재 여부
    boolean existsByFcmToken(String fcmToken);

    // 토큰으로 구독 조회
    Optional<PushSubscription> findByFcmToken(String fcmToken);
}
