package com.sookmyung.swapclass.domain.lounge.controller;

import com.sookmyung.swapclass.domain.lounge.dto.request.LoungePostCreateRequest;
import com.sookmyung.swapclass.domain.lounge.dto.request.LoungePostUpdateRequest;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungeBookmarkToggleResponse;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungeLikeToggleResponse;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostCreateResponse;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostDetailResponse;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostListResponse;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;
import com.sookmyung.swapclass.domain.lounge.service.LoungeBookmarkService;
import com.sookmyung.swapclass.domain.lounge.service.LoungeLikeService;
import com.sookmyung.swapclass.domain.lounge.service.LoungePostService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lounge/posts")
public class LoungePostController {

    private final LoungePostService loungePostService;
    private final LoungeLikeService loungeLikeService;
    private final LoungeBookmarkService loungeBookmarkService;

    // [목록 조회] 유형/과목태그/검색어 필터 (모두 선택), 최신순
    @GetMapping
    public ApiResponse<LoungePostListResponse> getList(
            @RequestParam(required = false) LoungePostType type,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(loungePostService.getList(type, courseId, keyword));
    }

    // [작성]
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LoungePostCreateResponse> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody LoungePostCreateRequest request) {
        return ApiResponse.success(loungePostService.create(userId, request), "게시글이 등록되었습니다.");
    }

    // [상세 조회]
    @GetMapping("/{postId}")
    public ApiResponse<LoungePostDetailResponse> getDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        return ApiResponse.success(loungePostService.getDetail(postId, userId));
    }

    // [수정] 본인 글만, 과목 태그는 수정 불가
    @PatchMapping("/{postId}")
    public ApiResponse<Void> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody LoungePostUpdateRequest request) {
        loungePostService.update(userId, postId, request);
        return ApiResponse.success(null, "게시글이 수정되었습니다.");
    }

    // [삭제] 본인 글만
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        loungePostService.delete(userId, postId);
        return ApiResponse.success(null, "게시글이 삭제되었습니다.");
    }

    // [좋아요 토글]
    @PostMapping("/{postId}/likes")
    public ApiResponse<LoungeLikeToggleResponse> toggleLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        return ApiResponse.success(loungeLikeService.toggle(userId, postId));
    }

    // [북마크 토글]
    @PostMapping("/{postId}/bookmarks")
    public ApiResponse<LoungeBookmarkToggleResponse> toggleBookmark(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        return ApiResponse.success(loungeBookmarkService.toggle(userId, postId));
    }
}
