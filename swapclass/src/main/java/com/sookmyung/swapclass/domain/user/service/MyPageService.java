package com.sookmyung.swapclass.domain.user.service;

import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import com.sookmyung.swapclass.domain.exchange.repository.ExchangeRepository;
import com.sookmyung.swapclass.domain.user.dto.request.PasswordChangeRequest;
import com.sookmyung.swapclass.domain.user.dto.response.MyInfoResponse;
import com.sookmyung.swapclass.domain.user.dto.response.NotificationSettingResponse;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.infra.redis.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // 기본 읽기 전용. 쓰기 메서드만 @Transactional로 덮음
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExchangeRepository exchangeRepository;
    private final RefreshTokenStore refreshTokenStore;

    // 탈퇴를 막는 '진행 중 거래' 상태
    private static final List<ExchangeStatus> ACTIVE_EXCHANGE_STATUSES =
            List.of(ExchangeStatus.IN_PROGRESS, ExchangeStatus.DISPUTE);

    // [내 정보 조회] 인증된 userId로 본인 정보 반환
    public MyInfoResponse getMyInfo(Long userId) {
        User user = findUser(userId);
        return MyInfoResponse.from(user);
    }

    // [알림 수신 토글] 본인의 알림 수신 설정 변경
    @Transactional
    public NotificationSettingResponse updateNotification(Long userId, boolean enabled) {
        User user = findUser(userId);
        user.updateNotificationEnabled(enabled);
        return NotificationSettingResponse.of(user.isNotificationEnabled());
    }

    // [비밀번호 변경] 현재 비밀번호 확인 후 새 비밀번호로 교체
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);   // 현재 비밀번호 불일치
        }
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);   // 새 비밀번호 확인 불일치
        }
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    // [회원 탈퇴] 진행 중 거래가 없을 때만 soft delete + refresh 토큰 삭제
    @Transactional
    public void withdraw(Long userId) {
        User user = findUser(userId);
        if (exchangeRepository.existsActiveByUser(userId, ACTIVE_EXCHANGE_STATUSES)) {
            throw new CustomException(ErrorCode.EXCHANGE_IN_PROGRESS);
        }
        user.withdraw();                    // status = WITHDRAWN (soft delete)
        refreshTokenStore.delete(userId);   // 로그인 유지 끊기
    }

    // 인증 토큰의 userId로 유저 조회(없으면 404)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
