package com.sookmyung.swapclass.domain.course.dto.response;

import com.sookmyung.swapclass.domain.course.entity.Course;

/**
 * 강의 검색/목록 항목. 게시글 작성·수정·졸업요건 등록 시 강의 선택용.
 * isGraduationReq   = 강의 카탈로그 차원의 졸업요건 태그(전체 공통).
 * myGraduationCourse = 현재 로그인 사용자가 본인 졸업요건으로 등록해 둔 과목인지 여부(개인화).
 */
public record LectureResponse(
        Long courseId,
        String name,
        String professor,
        String classTime,
        String courseType,
        String department,
        String category,
        String area,
        boolean isGraduationReq,
        boolean myGraduationCourse,
        String code,
        String section,
        String credits
) {
    public static LectureResponse from(Course course) {
        return from(course, false);
    }

    public static LectureResponse from(Course course, boolean myGraduationCourse) {
        return new LectureResponse(
                course.getId(),
                course.getName(),
                course.getProfessor(),
                course.getClassTime(),
                course.getCourseType(),
                course.getDepartment(),
                course.getCategory(),
                course.getArea(),
                course.isGraduationReq(),
                myGraduationCourse,
                course.getCode(),
                course.getSection(),
                course.getCredits()
        );
    }
}
