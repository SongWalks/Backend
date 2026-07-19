package com.sookmyung.swapclass.domain.graduation.controller;

import com.sookmyung.swapclass.domain.graduation.dto.request.GraduationCourseCreateRequest;
import com.sookmyung.swapclass.domain.graduation.dto.response.GraduationCompletionResponse;
import com.sookmyung.swapclass.domain.graduation.dto.response.GraduationCourseListResponse;
import com.sookmyung.swapclass.domain.graduation.service.GraduationCourseService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/graduation-courses")
public class GraduationCourseController {

    private final GraduationCourseService graduationCourseService;

    // [목록 조회] 내 졸업요건 과목 (q: 과목명 검색, 선택)
    @GetMapping
    public ApiResponse<GraduationCourseListResponse> getMyCourses(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String q) {
        return ApiResponse.success(graduationCourseService.getMyCourses(userId, q));
    }

    // [등록] 졸업요건 과목 추가 (중복 시 409)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> register(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody GraduationCourseCreateRequest request) {
        graduationCourseService.register(userId, request.courseId());
        return ApiResponse.success(null, "졸업요건 과목이 등록되었습니다.");
    }

    // [삭제] 등록된 졸업요건 과목 삭제
    @DeleteMapping("/{courseId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long courseId) {
        graduationCourseService.delete(userId, courseId);
        return ApiResponse.success(null, "졸업요건 과목이 삭제되었습니다.");
    }

    // [이수 완료 토글] 이수 여부 on/off
    @PatchMapping("/{courseId}")
    public ApiResponse<GraduationCompletionResponse> toggleCompleted(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long courseId) {
        return ApiResponse.success(graduationCourseService.toggleCompleted(userId, courseId));
    }
}
