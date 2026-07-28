package com.sookmyung.swapclass.domain.course.repository;

import com.sookmyung.swapclass.domain.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // [강의 검색/목록] keyword(강의명) · department · category · area · graduationOnly 선택 필터 + 페이지네이션.
    // 정렬: 내가 등록한 졸업요건 과목(myCourseIds)을 상단(0)으로, 그 안에서 강의명 오름차순.
    //  - myCourseIds 는 비어 있으면 안 됨(JPQL IN 제약). 비었을 땐 Service 에서 sentinel([-1]) 전달.
    //  - keyword 는 concat 패턴으로 바인딩(null 이면 필터 미적용).
    //  - countQuery 는 order by 를 제거하되, myCourseIds 파라미터 바인딩 유지를 위해 no-op 조건 포함.
    // NOTE: '학수번호'·'semester' 필터는 Course 엔티티에 컬럼이 없어 미구현.
    @Query(value = """
            select c from Course c
            where (:keyword is null or c.name like concat('%', :keyword, '%'))
              and (:department is null or c.department = :department)
              and (:category is null or c.category = :category)
              and (:area is null or c.area = :area)
              and (:graduationOnly = false or c.isGraduationReq = true)
            order by case when c.id in :myCourseIds then 0 else 1 end, c.name asc
            """,
            countQuery = """
            select count(c) from Course c
            where (:keyword is null or c.name like concat('%', :keyword, '%'))
              and (:department is null or c.department = :department)
              and (:category is null or c.category = :category)
              and (:area is null or c.area = :area)
              and (:graduationOnly = false or c.isGraduationReq = true)
              and (c.id in :myCourseIds or c.id not in :myCourseIds)
            """)
    Page<Course> searchLectures(@Param("keyword") String keyword,
                                @Param("department") String department,
                                @Param("category") String category,
                                @Param("area") String area,
                                @Param("graduationOnly") boolean graduationOnly,
                                @Param("myCourseIds") Collection<Long> myCourseIds,
                                Pageable pageable);
}
