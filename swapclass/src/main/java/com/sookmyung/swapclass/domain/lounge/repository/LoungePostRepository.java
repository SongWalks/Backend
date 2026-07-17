package com.sookmyung.swapclass.domain.lounge.repository;

import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoungePostRepository extends JpaRepository<LoungePost, Long> {

    // 유형(type) + 과목 태그(courseId) + 제목 검색어(keyword)를 함께 적용해 최신순 조회.
    // 각 조건은 null이면 무시된다(전체 조회).
    @Query("""
            SELECT p FROM LoungePost p
            WHERE (:type IS NULL OR p.type = :type)
              AND (:courseId IS NULL OR p.course.id = :courseId)
              AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%'))
            ORDER BY p.createdAt DESC
            """)
    List<LoungePost> search(@Param("type") LoungePostType type,
                            @Param("courseId") Long courseId,
                            @Param("keyword") String keyword);
}
