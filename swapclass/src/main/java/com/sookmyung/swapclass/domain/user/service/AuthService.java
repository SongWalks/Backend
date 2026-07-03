package com.sookmyung.swapclass.domain.user.service;

import com.sookmyung.swapclass.domain.user.dto.request.LoginRequest;
import com.sookmyung.swapclass.domain.user.dto.request.SignupRequest;
import com.sookmyung.swapclass.domain.user.dto.request.TokenRefreshRequest;
import com.sookmyung.swapclass.domain.user.dto.response.EmailExistsResponse;
import com.sookmyung.swapclass.domain.user.dto.response.TokenResponse;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.global.jwt.JwtTokenProvider;
import com.sookmyung.swapclass.infra.mail.EmailService;
import com.sookmyung.swapclass.infra.redis.AuthCodeStore;
import com.sookmyung.swapclass.infra.redis.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service                          // 스프링 서비스 빈으로 등록
@RequiredArgsConstructor          // final 필드들을 생성자 주입(아래 6개 의존성)
@Transactional(readOnly = true)   // 기본은 읽기 전용 트랜잭션. 쓰기 메서드만 아래서 @Transactional로 덮음
public class AuthService {

    // 필요한 도구들 주입: DB창구, Redis창구 2개, 메일, JWT도구, 비번암호화기
    private final UserRepository userRepository;
    private final AuthCodeStore authCodeStore;
    private final RefreshTokenStore refreshTokenStore;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private static final String SOOKMYUNG_DOMAIN = "@sookmyung.ac.kr"; // 숙명 도메인 검증용
    private static final int CODE_ORIGIN = 100000;   // 인증코드 최솟값(6자리 보장)
    private static final int CODE_BOUND = 900000;    // 난수 범위
    private static final SecureRandom RANDOM = new SecureRandom(); // 보안용 난수 생성기

    // [API 1] 이메일 인증코드 발송
    public void sendEmailCode(String email) {
        validateSookmyungEmail(email);                 // 숙명 이메일 아니면 403
        if (userRepository.existsByEmail(email)) {     // 이미 가입된 이메일이면 409
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        String code = generateCode();                  // 6자리 코드 생성
        authCodeStore.saveCode(email, code);           // Redis에 저장(5분 만료)
        emailService.sendAuthCode(email, code);        // 메일로 발송
    }

    // [API 2] 이메일 인증코드 확인
    public void verifyEmailCode(String email, String code) {
        String saved = authCodeStore.getCode(email);           // Redis에서 저장된 코드 꺼냄
        if (saved == null || !saved.equals(code)) {            // 없거나(만료) 불일치면 400
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        authCodeStore.markVerified(email);   // "인증완료" 플래그 저장(30분) → 회원가입까지 이어줌
        authCodeStore.deleteCode(email);     // 쓴 코드는 삭제
    }

    // [API 3] 이메일 중복확인 → boolean을 DTO로 감싸 반환
    public EmailExistsResponse checkEmailExists(String email) {
        return new EmailExistsResponse(userRepository.existsByEmail(email));
    }

    // [API 4] 회원가입 (DB에 쓰므로 @Transactional로 읽기전용 해제)
    @Transactional
    public void signup(SignupRequest request) {
        if (!request.password().equals(request.passwordConfirm())) {  // 비번≠비번확인 → 400
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        if (!authCodeStore.isVerified(request.email())) {             // 이메일 인증 안했으면 403
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if (userRepository.existsByEmail(request.email())) {          // 저장 직전 중복 재검증 409
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // 비번은 해시해서 저장(원문 X)
                .build();
        userRepository.save(user);                    // DB에 저장
        authCodeStore.deleteVerified(request.email()); // 인증완료 플래그 정리
    }

    // [API 5] 로그인
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)); // 없는 이메일 404
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {    // 비번 대조 실패 400
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        if (user.isSuspended()) {                       // 정지 계정이면 403
            throw new CustomException(ErrorCode.USER_SUSPENDED);
        }
        return issueTokens(user.getId());               // 토큰 2개 발급
    }

    // [API 6] 로그아웃 → Redis의 refresh 삭제(그 유저 로그인 유지 끊기)
    public void logout(Long userId) {
        refreshTokenStore.delete(userId);
    }

    // [API 7] 토큰 재발급
    public TokenResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();
        if (!jwtTokenProvider.validate(refreshToken)) {          // 서명·만료 검증 실패 401
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);  // 토큰에서 userId 꺼냄
        String saved = refreshTokenStore.get(userId);            // 서버에 저장된 refresh와 비교
        if (saved == null || !saved.equals(refreshToken)) {      // 다르면(이미 재로그인 등) 401
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return issueTokens(userId);                              // 새 토큰 발급(refresh도 교체)
    }

    // --- 내부 헬퍼들 ---

    // access + refresh 발급하고, refresh는 Redis에 저장 후 응답으로 반환
    private TokenResponse issueTokens(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        refreshTokenStore.save(userId, refreshToken);   // 저장(한 기기 정책: 이전 것 덮어씀)
        return new TokenResponse(accessToken, refreshToken);
    }

    // 숙명 이메일(@sookmyung.ac.kr)인지 검증
    private void validateSookmyungEmail(String email) {
        if (!email.endsWith(SOOKMYUNG_DOMAIN)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    // 100000~999999 범위의 6자리 인증코드 생성
    private String generateCode() {
        return String.valueOf(RANDOM.nextInt(CODE_BOUND) + CODE_ORIGIN);
    }
}
