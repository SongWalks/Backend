package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.course.entity.Course;

/**
 * 게시글 응답에 실리는 강의 요약 정보. (상세·피드 공용)
 */
public record CourseSummaryResponse(
        Long courseId,
        String name,
        String professor,
        String classTime,
        String department,
        String courseType
) {
    public static CourseSummaryResponse from(Course course) {
        return new CourseSummaryResponse(
                course.getId(),
                course.getName(),
                course.getProfessor(),
                course.getClassTime(),
                course.getDepartment(),
                course.getCourseType()
        );
    }
}
