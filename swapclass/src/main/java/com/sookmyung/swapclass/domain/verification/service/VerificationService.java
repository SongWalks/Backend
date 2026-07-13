package com.sookmyung.swapclass.domain.verification.service;

import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.domain.verification.dto.response.QrIssueResponse;
import com.sookmyung.swapclass.domain.verification.dto.response.VerifyUploadResponse;
import com.sookmyung.swapclass.domain.verification.entity.VerificationLog;
import com.sookmyung.swapclass.domain.verification.entity.VerifyStatus;
import com.sookmyung.swapclass.domain.verification.entity.VerifyType;
import com.sookmyung.swapclass.domain.verification.repository.VerificationLogRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.infra.qr.QrService;
import com.sookmyung.swapclass.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerificationService {

    private final VerificationLogRepository verificationLogRepository;
    private final UserRepository userRepository;
    private final QrService qrService;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String QR_TOKEN_PREFIX = "qr:token:";
    private static final long QR_EXPIRE_MINUTES = 10;

    // QR 토큰 발급 + QR 이미지 생성 + S3 업로드
    @Transactional
    public QrIssueResponse issueQr(Long exchangeId, Long userId) {
        // QR 토큰 생성 (UUID)
        String qrToken = UUID.randomUUID().toString();

        // Redis에 토큰 저장 (key: qr:token:{exchangeId}:{userId}, value: qrToken)
        String redisKey = QR_TOKEN_PREFIX + exchangeId + ":" + userId;
        redisTemplate.opsForValue().set(redisKey, qrToken, QR_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // QR 이미지 생성 (토큰값을 QR 내용으로)
        byte[] qrImageBytes = qrService.generateQrImage(qrToken);

        // S3 업로드
        String qrImageUrl = s3Service.uploadBytes(
                qrImageBytes,
                "qr",
                "qr_" + exchangeId + "_" + userId + ".png",
                "image/png"
        );

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(QR_EXPIRE_MINUTES);
        return new QrIssueResponse(qrToken, qrImageUrl, expiresAt);
    }

    // 캡처 이미지 업로드 + QR 검증
    @Transactional
    public VerifyUploadResponse uploadAndVerify(Long exchangeId, Long userId, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // S3에 캡처 이미지 업로드
        String imageUrl = s3Service.upload(image, "verification");

        // QR 디코딩
        String decodedToken = null;
        try {
            decodedToken = qrService.decodeQrImage(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("이미지 읽기 실패", e);
        }

        // Redis에서 저장된 QR 토큰 조회
        String redisKey = QR_TOKEN_PREFIX + exchangeId + ":" + userId;
        String savedToken = redisTemplate.opsForValue().get(redisKey);

        // 검증
        boolean qrValid = decodedToken != null && decodedToken.equals(savedToken);

        // verification_logs 저장
        VerificationLog log = VerificationLog.builder()
                .exchangeId(exchangeId)
                .user(user)
                .verifyType(VerifyType.PRE)
                .imageUrl(imageUrl)
                .build();

        if (qrValid) {
            log.pass();
            redisTemplate.delete(redisKey); // 검증 후 토큰 폐기
        } else {
            log.fail();
        }

        verificationLogRepository.save(log);

        // 양측 인증 완료 여부 확인
        long passedCount = verificationLogRepository
                .countByExchangeIdAndStatusAndVerifyType(exchangeId, VerifyStatus.PASSED, VerifyType.PRE);

        String message = qrValid ? "인증이 완료되었습니다." : "QR 코드를 확인할 수 없습니다.";
        if (qrValid && passedCount >= 2) {
            message = "양측 인증 완료! 카운트다운을 시작합니다.";
            // TODO: 채팅방 상태 → COUNTDOWN (chat_rooms 테이블 머지 후 구현)
        }

        return new VerifyUploadResponse(qrValid, qrValid ? "PASSED" : "FAILED", message);
    }
}
