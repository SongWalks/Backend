package com.sookmyung.swapclass.domain.course.repository;

import com.sookmyung.swapclass.domain.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // [필터 옵션] 학과/영역 드롭다운용. null·빈 문자열 제외, 가나다순.
    @Query("""
            select distinct c.department from Course c
            where c.department is not null and c.department <> ''
            order by c.department asc
            """)
    List<String> findDistinctDepartments();

    @Query("""
            select distinct c.area from Course c
            where c.area is not null and c.area <> ''
            order by c.area asc
            """)
    List<String> findDistinctAreas();

    // [필터 옵션] 강의 종류(전선·전필·교선 등) 드롭다운용.
    @Query("""
            select distinct c.category from Course c
            where c.category is not null and c.category <> ''
            order by c.category asc
            """)
    List<String> findDistinctCategories();

    // [강의 검색/목록] keyword(강의명) · department · category · area · graduationOnly 선택 필터 + 페이지네이션.
    // 정렬: 내 졸업요건 학수번호(myCodes)와 code 가 같은 과목을 상단(0)으로, 그 안에서 강의명 오름차순.
    //  - 같은 학수번호의 다른 분반도 함께 상단 노출된다(요청 사항).
    //  - myCodes 는 비어 있으면 안 됨(JPQL IN 제약). 비었을 땐 Service 에서 sentinel 전달.
    //  - keyword 는 concat 패턴으로 바인딩(null 이면 필터 미적용).
    //  - countQuery 는 order by 를 제거하되, myCodes 파라미터 바인딩 유지를 위해 no-op 조건 포함.
    // NOTE: 'semester' 필터는 Course 엔티티에 컬럼이 없어 미구현.
    @Query(value = """
            select c from Course c
            where (:keyword is null or c.name like concat('%', :keyword, '%'))
              and (:department is null or c.department = :department)
              and (:category is null or c.category = :category)
              and (:area is null or c.area = :area)
              and (:graduationOnly = false or c.isGraduationReq = true)
            order by case when c.code in :myCodes then 0 else 1 end, c.name asc
            """,
            countQuery = """
            select count(c) from Course c
            where (:keyword is null or c.name like concat('%', :keyword, '%'))
              and (:department is null or c.department = :department)
              and (:category is null or c.category = :category)
              and (:area is null or c.area = :area)
              and (:graduationOnly = false or c.isGraduationReq = true)
              and (c.code in :myCodes or c.code not in :myCodes or c.code is null)
            """)
    Page<Course> searchLectures(@Param("keyword") String keyword,
                                @Param("department") String department,
                                @Param("category") String category,
                                @Param("area") String area,
                                @Param("graduationOnly") boolean graduationOnly,
                                @Param("myCodes") Collection<String> myCodes,
                                Pageable pageable);
}
