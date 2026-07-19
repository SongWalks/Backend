package com.sookmyung.swapclass.domain.graduation.service;

import com.sookmyung.swapclass.domain.graduation.dto.response.GraduationCourseListResponse;
import com.sookmyung.swapclass.domain.graduation.entity.GraduationCourse;
import com.sookmyung.swapclass.domain.graduation.repository.GraduationCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GraduationCourseService {

    private final GraduationCourseRepository graduationCourseRepository;

    // [목록 조회] q 있으면 과목명 검색, 없으면 전체 (둘 다 최신순)
    public GraduationCourseListResponse getMyCourses(Long userId, String keyword) {
        List<GraduationCourse> courses = (keyword == null || keyword.isBlank())
                ? graduationCourseRepository.findByUserIdOrderByCreatedAtDesc(userId)
                : graduationCourseRepository.searchByUserIdAndCourseName(userId, keyword);
        return GraduationCourseListResponse.from(courses);
    }
}
