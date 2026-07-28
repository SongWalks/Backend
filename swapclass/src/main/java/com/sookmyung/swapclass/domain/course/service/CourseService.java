package com.sookmyung.swapclass.domain.course.service;

import com.sookmyung.swapclass.domain.course.dto.response.LectureResponse;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import com.sookmyung.swapclass.domain.graduation.repository.GraduationCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final GraduationCourseRepository graduationCourseRepository;

    // 강의 검색/목록 (모든 필터 선택). 강의명 오름차순 기본,
    // 단 로그인 사용자가 본인 졸업요건으로 등록한 과목은 (필터 통과 시) 상단으로 끌어올림.
    public List<LectureResponse> searchLectures(Long userId, String keyword, String department,
                                                String category, String area, boolean graduationOnly) {
        var courses = courseRepository
                .searchLectures(keyword, department, category, area, graduationOnly);

        // 내 졸업요건으로 등록된 course id 집합 (없으면 빈 집합)
        Set<Long> myCourseIds = Set.copyOf(graduationCourseRepository.findCourseIdsByUserId(userId));

        // 필터 결과를 유지한 채, 내 졸업요건 과목을 앞으로 분리(각 그룹은 강의명 오름차순 유지)
        List<LectureResponse> mine = new ArrayList<>();
        List<LectureResponse> others = new ArrayList<>();
        for (var course : courses) {
            boolean isMine = myCourseIds.contains(course.getId());
            (isMine ? mine : others).add(LectureResponse.from(course, isMine));
        }
        mine.addAll(others);
        return mine;
    }
}
