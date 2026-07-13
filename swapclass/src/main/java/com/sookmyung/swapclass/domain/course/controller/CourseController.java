package com.sookmyung.swapclass.domain.course.controller;

import com.sookmyung.swapclass.domain.course.dto.response.LectureResponse;
import com.sookmyung.swapclass.domain.course.service.CourseService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
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

    // 강의 검색/목록 조회 (게시글 작성·수정·졸업요건 등록 시 강의 선택용)
    @GetMapping
    public ApiResponse<List<LectureResponse>> searchLectures(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "false") boolean graduationOnly
    ) {
        return ApiResponse.success(
                courseService.searchLectures(keyword, department, category, area, graduationOnly)
        );
    }
}
