package com.sookmyung.swapclass.domain.verification.repository;

import com.sookmyung.swapclass.domain.verification.entity.VerificationLog;
import com.sookmyung.swapclass.domain.verification.entity.VerifyStatus;
import com.sookmyung.swapclass.domain.verification.entity.VerifyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VerificationLogRepository extends JpaRepository<VerificationLog, Long> {

    // 특정 교환의 특정 유저 인증 조회
    Optional<VerificationLog> findByExchangeIdAndUserIdAndVerifyType(
            Long exchangeId, Long userId, VerifyType verifyType);

    // 특정 교환의 전체 인증 내역
    List<VerificationLog> findByExchangeId(Long exchangeId);

    // 특정 교환에서 PASSED 인증 수 (양측 완료 여부 확인용)
    long countByExchangeIdAndStatusAndVerifyType(
            Long exchangeId,
            com.sookmyung.swapclass.domain.verification.entity.VerifyStatus status,
            VerifyType verifyType);

    @Query("SELECT COUNT(DISTINCT v.user.id) FROM VerificationLog v WHERE v.exchangeId = :exchangeId AND v.status = :status AND v.verifyType = :verifyType")
    long countDistinctUserByExchangeIdAndStatusAndVerifyType(
            @Param("exchangeId") Long exchangeId,
            @Param("status") VerifyStatus status,
            @Param("verifyType") VerifyType verifyType);

    //QR 인증 후 상대방의 캡처 이미지 조회
    Optional<VerificationLog> findTopByExchangeIdAndUserIdOrderByCreatedAtDesc(Long exchangeId, Long userId);
}
