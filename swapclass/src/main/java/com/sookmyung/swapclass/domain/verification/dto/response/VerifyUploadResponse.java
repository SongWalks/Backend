package com.sookmyung.swapclass.domain.verification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyUploadResponse {
    private boolean qrValid;
    private String status;
    private String message;
}
