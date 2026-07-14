package com.sookmyung.swapclass.domain.post.repository;

import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostWantedCourseRepository extends JpaRepository<PostWantedCourse, Long> {

    // [퀵필터 보조] 내가 want(1~3순위)로 등록한 과목 id 목록 (활성 게시글 기준)
    @Query("select w.course.id from PostWantedCourse w where w.post.user.id = :userId and w.post.status = :status")
    List<Long> findMyWantCourseIds(@Param("userId") Long userId, @Param("status") PostStatus status);
}
