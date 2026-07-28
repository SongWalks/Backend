package com.sookmyung.swapclass.domain.inquiry.controller;

import com.sookmyung.swapclass.domain.inquiry.dto.request.InquiryRequest;
import com.sookmyung.swapclass.domain.inquiry.service.InquiryService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의 접수
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createInquiry(
            @Valid @RequestBody InquiryRequest request,
            @AuthenticationPrincipal Long userId) {
        Long inquiryId = inquiryService.createInquiry(userId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(Map.of("inquiryId", inquiryId), "문의가 접수되었습니다."));
    }
}
