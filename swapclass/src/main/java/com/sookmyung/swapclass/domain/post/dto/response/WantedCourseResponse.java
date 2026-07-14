package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

/**
 * 원하는 과목 한 건: 우선순위(1~3) + 강의 요약. (상세·찜목록·피드 공용)
 */
public record WantedCourseResponse(int priority, CourseSummaryResponse course) {

    public static WantedCourseResponse from(PostWantedCourse wantedCourse) {
        return new WantedCourseResponse(
                wantedCourse.getPriority(),
                CourseSummaryResponse.from(wantedCourse.getCourse())
        );
    }
}
