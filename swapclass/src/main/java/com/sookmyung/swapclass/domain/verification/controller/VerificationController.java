package com.sookmyung.swapclass.domain.verification.controller;

import com.sookmyung.swapclass.domain.verification.dto.response.QrIssueResponse;
import com.sookmyung.swapclass.domain.verification.dto.response.VerifyUploadResponse;
import com.sookmyung.swapclass.domain.verification.service.VerificationService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    // QR 토큰 발급
    @PostMapping("/{exchangeId}/verifications/qr")
    public ResponseEntity<ApiResponse<QrIssueResponse>> issueQr(
            @PathVariable Long exchangeId,
            @AuthenticationPrincipal Long userId) {
        QrIssueResponse response = verificationService.issueQr(exchangeId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 캡처 업로드 + QR 검증
    @PostMapping("/{exchangeId}/verifications/capture")
    public ResponseEntity<ApiResponse<VerifyUploadResponse>> uploadAndVerify(
            @PathVariable Long exchangeId,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal Long userId) {
        VerifyUploadResponse response = verificationService.uploadAndVerify(exchangeId, userId, image);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
