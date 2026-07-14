package com.sookmyung.swapclass.domain.course.service;

import com.sookmyung.swapclass.domain.course.dto.response.LectureResponse;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    // 강의 검색/목록 (모든 필터 선택). 강의명 오름차순.
    public List<LectureResponse> searchLectures(String keyword, String department,
                                                String category, String area, boolean graduationOnly) {
        return courseRepository
                .searchLectures(keyword, department, category, area, graduationOnly)
                .stream()
                .map(LectureResponse::from)
                .toList();
    }
}
