package com.sookmyung.swapclass.domain.course.controller;

import com.sookmyung.swapclass.domain.course.dto.response.FilterOptionResponse;
import com.sookmyung.swapclass.domain.course.dto.response.LectureResponse;
import com.sookmyung.swapclass.domain.course.service.CourseService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import com.sookmyung.swapclass.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 강의 검색/목록 조회 (게시글 작성·수정·졸업요건 등록 시 강의 선택용), 페이지네이션.
    // 파라미터 없이 호출하면 전체 목록 첫 페이지. 내가 등록한 졸업요건 과목(myGraduationCourse=true)이 상단에 노출됨.
    @GetMapping
    public ApiResponse<PageResponse<LectureResponse>> searchLectures(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String area,
            @RequestParam(required = false, defaultValue = "false") Boolean graduationOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(
                courseService.searchLectures(userId, keyword, department, category, area,
                        Boolean.TRUE.equals(graduationOnly), page, size)
        );
    }

    // 학과/영역 드롭다운 옵션. 학과전공(type=DEPARTMENT)과 교양 영역(type=AREA)을 통합 반환.
    // 프론트는 선택한 옵션의 type 에 따라 검색 시 department= 또는 area= 로 전달.
    @GetMapping("/departments")
    public ApiResponse<List<FilterOptionResponse>> getDepartmentAreaOptions() {
        return ApiResponse.success(courseService.getDepartmentAreaOptions());
    }

    // 강의 종류(전선·전필·교선 등) 드롭다운 옵션.
    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategoryOptions() {
        return ApiResponse.success(courseService.getCategoryOptions());
    }
}
