package com.sookmyung.swapclass.domain.user.service;

import com.sookmyung.swapclass.domain.user.dto.request.PasswordChangeRequest;
import com.sookmyung.swapclass.domain.user.dto.response.MyInfoResponse;
import com.sookmyung.swapclass.domain.user.dto.response.NotificationSettingResponse;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // 기본 읽기 전용. 쓰기 메서드만 @Transactional로 덮음
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    // 인증 토큰의 userId로 유저 조회(없으면 404)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
