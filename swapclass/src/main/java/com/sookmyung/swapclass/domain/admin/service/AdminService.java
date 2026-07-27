package com.sookmyung.swapclass.domain.admin.service;

import com.sookmyung.swapclass.domain.admin.dto.request.SanctionRequest;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public LocalDateTime sanctionUser(Long userId, SanctionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime suspendedUntil = null;

        switch (request.getSuspendDays()) {
            case "3" -> {
                suspendedUntil = LocalDateTime.now().plusDays(3);
                user.suspend(suspendedUntil);
            }
            case "semester" -> {
                // 학기 수강정정기간 정지 (임시로 90일)
                suspendedUntil = LocalDateTime.now().plusDays(90);
                user.suspend(suspendedUntil);
            }
            case "permanent" -> {
                // 영구 차단 — 아주 먼 미래로 설정
                suspendedUntil = LocalDateTime.of(9999, 12, 31, 0, 0, 0);
                user.suspend(suspendedUntil);
            }
            default -> throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return suspendedUntil;
    }
}
