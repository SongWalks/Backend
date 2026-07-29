package com.sookmyung.swapclass.domain.course.service;

import com.sookmyung.swapclass.domain.course.dto.response.FilterOptionResponse;
import com.sookmyung.swapclass.domain.course.dto.response.LectureResponse;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import com.sookmyung.swapclass.domain.graduation.repository.GraduationCourseRepository;
import com.sookmyung.swapclass.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    // myCourseIds 가 비었을 때 JPQL IN 제약 회피용 sentinel (course id 는 양수라 매칭되지 않음)
    private static final Collection<Long> EMPTY_SENTINEL = List.of(-1L);

    private final CourseRepository courseRepository;
    private final GraduationCourseRepository graduationCourseRepository;

    // 강의 검색/목록 (모든 필터 선택) + 페이지네이션.
    // 내가 졸업요건으로 등록한 과목은 (필터 통과 시) 상단으로, myGraduationCourse=true 로 표시.
    public PageResponse<LectureResponse> searchLectures(Long userId, String keyword, String department,
                                                        String category, String area,
                                                        boolean graduationOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 내 졸업요건 course id 집합 (플래그 판정용). 쿼리에는 비어있지 않은 컬렉션을 넘김.
        Set<Long> myCourseIds = Set.copyOf(graduationCourseRepository.findCourseIdsByUserId(userId));
        Collection<Long> queryIds = myCourseIds.isEmpty() ? EMPTY_SENTINEL : myCourseIds;

        Page<LectureResponse> result = courseRepository
                .searchLectures(keyword, department, category, area, graduationOnly, queryIds, pageable)
                .map(course -> LectureResponse.from(course, myCourseIds.contains(course.getId())));

        return PageResponse.from(result);
    }

    // [필터 옵션] 학과/영역 드롭다운. 학과전공(department) + 교양 영역(area)을 type 태그와 함께 통합 반환.
    public List<FilterOptionResponse> getDepartmentAreaOptions() {
        List<FilterOptionResponse> options = new ArrayList<>();
        courseRepository.findDistinctDepartments().forEach(d -> options.add(FilterOptionResponse.department(d)));
        courseRepository.findDistinctAreas().forEach(a -> options.add(FilterOptionResponse.area(a)));
        return options;
    }

    // [필터 옵션] 강의 종류(전선·전필·교선 등) 드롭다운.
    public List<String> getCategoryOptions() {
        return courseRepository.findDistinctCategories();
    }
}
