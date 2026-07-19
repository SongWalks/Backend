package com.sookmyung.swapclass.domain.graduation.dto.response;

import com.sookmyung.swapclass.domain.graduation.entity.GraduationCourse;

import java.util.List;

// 졸업요건 과목 목록 응답 (data.courses[])
public record GraduationCourseListResponse(
        List<GraduationCourseResponse> courses
) {
    public static GraduationCourseListResponse from(List<GraduationCourse> graduationCourses) {
        List<GraduationCourseResponse> courses = graduationCourses.stream()
                .map(GraduationCourseResponse::from)
                .toList();
        return new GraduationCourseListResponse(courses);
    }
}
