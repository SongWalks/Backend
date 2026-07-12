package com.sookmyung.swapclass.domain.exchange.controller;

import com.sookmyung.swapclass.domain.exchange.dto.request.CancelRequest;
import com.sookmyung.swapclass.domain.exchange.dto.request.ResultRequest;
import com.sookmyung.swapclass.domain.exchange.dto.request.ScheduleRequest;
import com.sookmyung.swapclass.domain.exchange.dto.response.ResultResponse;
import com.sookmyung.swapclass.domain.exchange.dto.response.ScheduleResponse;
import com.sookmyung.swapclass.domain.exchange.service.ExchangeService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    // 교환 시간 확정
    @PostMapping("/{exchangeId}/schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> confirmSchedule(
            @PathVariable Long exchangeId,
            @Valid @RequestBody ScheduleRequest request,
            @AuthenticationPrincipal Long userId) {
        ScheduleResponse response = exchangeService.confirmSchedule(exchangeId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "교환 시간이 확정되었습니다."));
    }

    // 교환 결과 선택
    @PostMapping("/{exchangeId}/result")
    public ResponseEntity<ApiResponse<ResultResponse>> selectResult(
            @PathVariable Long exchangeId,
            @Valid @RequestBody ResultRequest request,
            @AuthenticationPrincipal Long userId) {
        ResultResponse response = exchangeService.selectResult(exchangeId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 거래 파기
    @PostMapping("/{exchangeId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelExchange(
            @PathVariable Long exchangeId,
            @Valid @RequestBody CancelRequest request,
            @AuthenticationPrincipal Long userId) {
        exchangeService.cancelExchange(exchangeId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "거래가 파기되었습니다."));
    }
}
