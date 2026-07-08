package com.sookmyung.swapclass.domain.block.controller;

import com.sookmyung.swapclass.domain.block.dto.response.BlockResponse;
import com.sookmyung.swapclass.domain.block.service.BlockService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    // 유저 차단
    @PostMapping("/{userId}/blocks")
    public ResponseEntity<ApiResponse<BlockResponse>> blockUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal Long currentUserId) {
        BlockResponse response = blockService.blockUser(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "사용자를 차단했습니다."));
    }

    // 차단 해제
    @DeleteMapping("/{userId}/blocks")
    public ResponseEntity<ApiResponse<BlockResponse>> unblockUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal Long currentUserId) {
        BlockResponse response = blockService.unblockUser(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "차단을 해제했습니다."));
    }
}
