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

    // myCodes 가 비었을 때 JPQL IN 제약 회피용 sentinel (실제 학수번호와 겹치지 않는 값)
    private static final Collection<String> EMPTY_SENTINEL = List.of("__NONE__");

    private final CourseRepository courseRepository;
    private final GraduationCourseRepository graduationCourseRepository;

    // 강의 검색/목록 (모든 필터 선택) + 페이지네이션.
    // 내 졸업요건 과목과 같은 학수번호(code)를 가진 과목은 (다른 분반 포함) 상단으로, myGraduationCourse=true 로 표시.
    public PageResponse<LectureResponse> searchLectures(Long userId, String keyword, String department,
                                                        String category, String area,
                                                        boolean graduationOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 내 졸업요건 학수번호 집합 (플래그 판정용). 쿼리에는 비어있지 않은 컬렉션을 넘김.
        Set<String> myCodes = Set.copyOf(graduationCourseRepository.findCourseCodesByUserId(userId));
        Collection<String> queryCodes = myCodes.isEmpty() ? EMPTY_SENTINEL : myCodes;

        Page<LectureResponse> result = courseRepository
                .searchLectures(keyword, department, category, area, graduationOnly, queryCodes, pageable)
                .map(course -> LectureResponse.from(course, course.getCode() != null && myCodes.contains(course.getCode())));

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
