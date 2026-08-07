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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Map;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerificationService {

    private final VerificationLogRepository verificationLogRepository;
    private final UserRepository userRepository;
    private final QrService qrService;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String QR_TOKEN_PREFIX = "qr:token:";
    private static final long QR_EXPIRE_MINUTES = 10;

    @Transactional
    public QrIssueResponse issueQr(Long exchangeId, Long userId) {
        String redisKey = QR_TOKEN_PREFIX + exchangeId + ":" + userId;

        // 기존 유효한 토큰이 있으면 그대로 반환
        String existingToken = redisTemplate.opsForValue().get(redisKey);
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);

        if (existingToken != null && ttl != null && ttl > 0) {
            byte[] qrImageBytes = qrService.generateQrImage(existingToken);
            String qrImageUrl = s3Service.uploadBytes(
                    qrImageBytes,
                    "qr",
                    "qr_" + exchangeId + "_" + userId + ".png",
                    "image/png"
            );
            ZonedDateTime expiresAt = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(ttl);
            return new QrIssueResponse(existingToken, qrImageUrl, expiresAt);
        }

        // 새 토큰 발급
        String qrToken = UUID.randomUUID().toString();
            // Redis에 토큰 저장 (key: qr:token:{exchangeId}:{userId}, value: qrToken)
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

        ZonedDateTime expiresAt = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(QR_EXPIRE_MINUTES);
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

        // Redis에서 저장된 QR 토큰 조회 (본인 userId로 조회)
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
        //고유 userId 기준으로 카운트 (같은 유저의 PASSED가 2개인 경우 대비)
        long passedCount = verificationLogRepository
                .countDistinctUserByExchangeIdAndStatusAndVerifyType(exchangeId, VerifyStatus.PASSED, VerifyType.PRE);

        String message;
        if (!qrValid) {
            message = "인증 QR 코드를 확인할 수 없습니다.\n수강신청(1학년) 페이지의 인증 QR 코드가 한 화면에 모두 보이도록 한 뒤 다시 인증을 진행해주세요.";
        } else if (passedCount >= 2) {
            message = "양측 인증 완료! 카운트다운을 시작합니다.";
            chatRoomRepository.findByExchangeId(exchangeId)
                    .ifPresent(chatRoom -> {
                        chatRoom.changeStatus(ChatRoomStatus.COUNTDOWN);
                        // WebSocket으로 카운트다운 시작 신호 발송
                        messagingTemplate.convertAndSend(
                                "/topic/chat-rooms/" + chatRoom.getId(),
                                Map.of("type", "COUNTDOWN_START", "seconds", 10)
                        );
                    });
        } else {
            message = "인증이 완료되었습니다.";  // ← 한쪽만 인증 완료된 경우
        }

        return new VerifyUploadResponse(qrValid, qrValid ? "PASSED" : "FAILED", message);
    }
}
