package com.sookmyung.swapclass.domain.course.repository;

import com.sookmyung.swapclass.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // [강의 검색] 강좌명(q) · 학과(dept) 선택 필터
    // NOTE: 명세의 '학수번호' 검색과 'semester' 필터는 Course 엔티티에 해당 컬럼이 없어 미구현.
    //       컬럼 추가 여부는 course 도메인 담당자와 확인 필요.
    @Query("""
            select c from Course c
            where (:q is null or c.name like %:q%)
              and (:dept is null or c.department = :dept)
            """)
    List<Course> search(@Param("q") String q, @Param("dept") String dept);
}
