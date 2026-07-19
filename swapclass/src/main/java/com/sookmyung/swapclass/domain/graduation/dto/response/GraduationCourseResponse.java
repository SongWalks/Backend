package com.sookmyung.swapclass.domain.graduation.dto.response;

import com.sookmyung.swapclass.domain.graduation.entity.GraduationCourse;

// 졸업요건 과목 목록 항목 (courseId는 삭제/이수토글 시 식별자로 사용)
public record GraduationCourseResponse(
        Long id,
        Long courseId,
        String courseName,
        boolean completed
) {
    public static GraduationCourseResponse from(GraduationCourse graduationCourse) {
        return new GraduationCourseResponse(
                graduationCourse.getId(),
                graduationCourse.getCourse().getId(),
                graduationCourse.getCourse().getName(),
                graduationCourse.isCompleted()
        );
    }
}
