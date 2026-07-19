package com.sookmyung.swapclass.domain.graduation.repository;

import com.sookmyung.swapclass.domain.graduation.entity.GraduationCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GraduationCourseRepository extends JpaRepository<GraduationCourse, Long> {

    // 중복 등록 체크
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // 삭제·토글 대상 조회
    Optional<GraduationCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    // 내 졸업요건 과목 전체 (최신순)
    List<GraduationCourse> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 과목명 검색 (q) — 최신순
    @Query("select g from GraduationCourse g join g.course c " +
            "where g.user.id = :userId and c.name like %:keyword% " +
            "order by g.createdAt desc")
    List<GraduationCourse> searchByUserIdAndCourseName(@Param("userId") Long userId,
                                                       @Param("keyword") String keyword);
}
