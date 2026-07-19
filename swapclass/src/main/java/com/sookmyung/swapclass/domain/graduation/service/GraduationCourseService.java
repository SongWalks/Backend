package com.sookmyung.swapclass.domain.graduation.service;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import com.sookmyung.swapclass.domain.graduation.dto.response.GraduationCourseListResponse;
import com.sookmyung.swapclass.domain.graduation.entity.GraduationCourse;
import com.sookmyung.swapclass.domain.graduation.repository.GraduationCourseRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GraduationCourseService {

    private final GraduationCourseRepository graduationCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // [목록 조회] q 있으면 과목명 검색, 없으면 전체 (둘 다 최신순)
    public GraduationCourseListResponse getMyCourses(Long userId, String keyword) {
        List<GraduationCourse> courses = (keyword == null || keyword.isBlank())
                ? graduationCourseRepository.findByUserIdOrderByCreatedAtDesc(userId)
                : graduationCourseRepository.searchByUserIdAndCourseName(userId, keyword);
        return GraduationCourseListResponse.from(courses);
    }

    // [등록] 이미 등록된 과목이면 409, 없는 과목이면 404
    @Transactional
    public void register(Long userId, Long courseId) {
        if (graduationCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ErrorCode.GRADUATION_COURSE_DUPLICATED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        graduationCourseRepository.save(
                GraduationCourse.builder().user(user).course(course).build());
    }

    // [삭제] 등록되지 않은 과목이면 404
    @Transactional
    public void delete(Long userId, Long courseId) {
        GraduationCourse graduationCourse = graduationCourseRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.GRADUATION_COURSE_NOT_FOUND));
        graduationCourseRepository.delete(graduationCourse);
    }
}
