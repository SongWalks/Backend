package com.sookmyung.swapclass.domain.push.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PushSubscribeRequest {

    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private String fcmToken;

    private String deviceType; // WEB, ANDROID, IOS
}
