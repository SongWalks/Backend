package com.sookmyung.swapclass.domain.user.service;

import com.sookmyung.swapclass.domain.user.dto.response.MyInfoResponse;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // 기본 읽기 전용. 쓰기 메서드만 @Transactional로 덮음
public class MyPageService {

    private final UserRepository userRepository;

    // [내 정보 조회] 인증된 userId로 본인 정보 반환
    public MyInfoResponse getMyInfo(Long userId) {
        User user = findUser(userId);
        return MyInfoResponse.from(user);
    }

    // 인증 토큰의 userId로 유저 조회(없으면 404)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
