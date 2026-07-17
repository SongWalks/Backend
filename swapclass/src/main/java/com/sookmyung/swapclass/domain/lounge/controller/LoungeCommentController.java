package com.sookmyung.swapclass.domain.lounge.controller;

import com.sookmyung.swapclass.domain.lounge.dto.request.LoungeCommentCreateRequest;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungeCommentCreateResponse;
import com.sookmyung.swapclass.domain.lounge.service.LoungeCommentService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lounge")
public class LoungeCommentController {

    private final LoungeCommentService loungeCommentService;

    // [댓글 작성] POST /api/lounge/posts/{postId}/comments
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LoungeCommentCreateResponse> create(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody LoungeCommentCreateRequest request) {
        return ApiResponse.success(loungeCommentService.create(userId, postId, request), "댓글이 등록되었습니다.");
    }

    // [댓글 삭제] DELETE /api/lounge/comments/{commentId} — 본인 댓글만
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long commentId) {
        loungeCommentService.delete(userId, commentId);
        return ApiResponse.success(null, "댓글이 삭제되었습니다.");
    }
}
