package com.sookmyung.swapclass.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

// 알림 수신 설정 토글 요청
public record NotificationToggleRequest(
        @NotNull Boolean notificationEnabled
) {}
