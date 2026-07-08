package com.sookmyung.swapclass.domain.report.controller;

import com.sookmyung.swapclass.domain.report.dto.request.ReportRequest;
import com.sookmyung.swapclass.domain.report.service.ReportService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 임시 userId — JWT 구현 후 SecurityContext로 교체 예정
    private static final Long TEMP_USER_ID = 1L;

    // 신고 접수
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createReport(
            @Valid @RequestBody ReportRequest request) {

        Long reportId = reportService.createReport(TEMP_USER_ID, request);

        return ResponseEntity.status(201)
                .body(ApiResponse.success(Map.of("reportId", reportId), "신고가 접수되었습니다."));
    }
}