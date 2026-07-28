package com.sookmyung.swapclass.domain.admin.controller;

import com.sookmyung.swapclass.domain.admin.dto.request.SanctionRequest;
import com.sookmyung.swapclass.domain.admin.service.AdminService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 사용자 제재(정지) 처리
    @PatchMapping("/users/{userId}/sanctions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sanctionUser(
            @PathVariable Long userId,
            @Valid @RequestBody SanctionRequest request) {
        LocalDateTime suspendedUntil = adminService.sanctionUser(userId, request);

        Map<String, Object> data = new HashMap<>();
        data.put("suspendedUntil", suspendedUntil);

        return ResponseEntity.ok(ApiResponse.success(data, "제재가 처리되었습니다."));
    }
}
