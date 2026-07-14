package com.sookmyung.swapclass.domain.verification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QrIssueResponse {
    private String qrToken;
    private String qrImageUrl;
    private LocalDateTime expiresAt;
}
