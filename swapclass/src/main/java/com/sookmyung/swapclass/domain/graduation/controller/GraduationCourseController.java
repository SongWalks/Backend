package com.sookmyung.swapclass.domain.graduation.controller;

import com.sookmyung.swapclass.domain.graduation.dto.response.GraduationCourseListResponse;
import com.sookmyung.swapclass.domain.graduation.service.GraduationCourseService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
}
