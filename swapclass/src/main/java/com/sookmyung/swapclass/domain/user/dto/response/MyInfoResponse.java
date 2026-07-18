package com.sookmyung.swapclass.domain.user.dto.response;

import com.sookmyung.swapclass.domain.user.entity.User;

// 마이페이지 내 정보 조회 응답
public record MyInfoResponse(
        Long id,
        String email,
        String nickname,
        int penaltyCount,
        int mannerWarningCount,
        String status,
        boolean notificationEnabled
) {
    public static MyInfoResponse from(User user) {
        return new MyInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPenaltyCount(),
                user.getMannerWarningCount(),
                user.getStatus().name(),
                user.isNotificationEnabled()
        );
    }
}
