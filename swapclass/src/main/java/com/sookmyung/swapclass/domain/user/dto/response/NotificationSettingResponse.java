package com.sookmyung.swapclass.domain.user.dto.response;

// 알림 수신 설정 변경 결과 응답
public record NotificationSettingResponse(
        boolean notificationEnabled
) {
    public static NotificationSettingResponse of(boolean notificationEnabled) {
        return new NotificationSettingResponse(notificationEnabled);
    }
}
