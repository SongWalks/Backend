package com.sookmyung.swapclass.domain.course.repository;

import com.sookmyung.swapclass.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // [강의 검색/목록] keyword(강의명) · department · category · area · graduationOnly 선택 필터
    // NOTE: 명세의 '졸업요건 등록 과목 최상단 노출'은 graduation_courses(타 도메인) 의존이라 미구현.
    //       '학수번호'·'semester' 필터도 Course 엔티티에 컬럼이 없어 미구현.
    @Query("""
            select c from Course c
            where (:keyword is null or c.name like %:keyword%)
              and (:department is null or c.department = :department)
              and (:category is null or c.category = :category)
              and (:area is null or c.area = :area)
              and (:graduationOnly = false or c.isGraduationReq = true)
            order by c.name asc
            """)
    List<Course> searchLectures(@Param("keyword") String keyword,
                                @Param("department") String department,
                                @Param("category") String category,
                                @Param("area") String area,
                                @Param("graduationOnly") boolean graduationOnly);
}
