package com.sookmyung.swapclass.domain.course.dto.response;

import com.sookmyung.swapclass.domain.course.entity.Course;

/**
 * 강의 검색/목록 항목. 게시글 작성·수정·졸업요건 등록 시 강의 선택용.
 * isGraduationReq = 졸업요건 태그.
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
        boolean isGraduationReq
) {
    public static LectureResponse from(Course course) {
        return new LectureResponse(
                course.getId(),
                course.getName(),
                course.getProfessor(),
                course.getClassTime(),
                course.getCourseType(),
                course.getDepartment(),
                course.getCategory(),
                course.getArea(),
                course.isGraduationReq()
        );
    }
}
