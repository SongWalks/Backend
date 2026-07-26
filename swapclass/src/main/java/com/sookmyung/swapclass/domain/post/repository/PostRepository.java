package com.sookmyung.swapclass.domain.post.repository;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.block.entity.UserBlock;
import com.sookmyung.swapclass.domain.match.dto.MatchCandidateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // [피드] 특정 상태(MATCHABLE) · 본인 글 제외 · 차단 유저 양방향 제외 · 학과 필터(선택) · 최신순 페이징
    @Query("""
        select p from Post p
        where p.status = :status
          and p.user.id <> :userId
          and p.user.id not in (
              select b.blocked.id from UserBlock b where b.blocker.id = :userId
          )
          and p.user.id not in (
              select b.blocker.id from UserBlock b where b.blocked.id = :userId
          )
          and (:dept is null or p.discardCourse.department = :dept)
        order by p.createdAt desc
        """)
    Page<Post> findFeed(@Param("status") PostStatus status,
                        @Param("userId") Long userId,
                        @Param("dept") String dept,
                        Pageable pageable);

    // [내 게시글] 상태 필터 있음
    List<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);

    // [내 게시글] 전체(삭제 제외)
    List<Post> findByUserIdAndStatusNotOrderByCreatedAtDesc(Long userId, PostStatus status);

    // [퀵필터: my-targets] 내 want 과목(courseIds)을 '버릴 과목'으로 올린 타 유저 글
    @Query("""
            select p from Post p
            where p.status = :status
              and p.user.id <> :userId
              and p.discardCourse.id in :courseIds
            order by p.createdAt desc
            """)
    List<Post> findMyTargets(@Param("status") PostStatus status,
                             @Param("userId") Long userId,
                             @Param("courseIds") Collection<Long> courseIds);

    // [퀵필터: my-seekers] 내 '버릴 과목'(courseIds)을 want 로 찾는 타 유저 글
    @Query("""
            select distinct p from Post p
            join p.wantedCourses w
            where p.status = :status
              and p.user.id <> :userId
              and w.course.id in :courseIds
            order by p.createdAt desc
            """)
    List<Post> findMySeekers(@Param("status") PostStatus status,
                             @Param("userId") Long userId,
                             @Param("courseIds") Collection<Long> courseIds);

    // [퀵필터 보조] 내가 올린 '버릴 과목' id 목록 (활성 게시글 기준)
    @Query("select p.discardCourse.id from Post p where p.user.id = :userId and p.status = :status")
    List<Long> findMyGiveCourseIds(@Param("userId") Long userId, @Param("status") PostStatus status);

    // [홈 추천 피드] 양방향 교집합. 후보글 p가 추천되려면 내 게시글 m 중
    //  (상대 버릴 과목 ∈ 내 원하는 과목) AND (내 버릴 과목 ∈ 상대 원하는 과목) 인 쌍이 하나라도 존재해야 함.
    //  본인 글 제외 · 차단 유저 양방향 제외 · 이미 거절/무산된 쌍(MatchIgnore) 제외 · 최신순 페이징.
    @Query(value = """
        select p from Post p
        where p.status = :status
          and p.user.id <> :userId
          and p.user.id not in (
              select b.blocked.id from UserBlock b where b.blocker.id = :userId
          )
          and p.user.id not in (
              select b.blocker.id from UserBlock b where b.blocked.id = :userId
          )
          and exists (
              select 1 from Post m
              where m.user.id = :userId
                and m.status = :status
                and m.discardCourse.id in (
                    select pw.course.id from PostWantedCourse pw where pw.post = p
                )
                and p.discardCourse.id in (
                    select mw.course.id from PostWantedCourse mw where mw.post = m
                )
                and not exists (
                    select 1 from MatchIgnore mi
                    where (mi.postA.id = m.id and mi.postB.id = p.id)
                       or (mi.postA.id = p.id and mi.postB.id = m.id)
                )
          )
        order by p.createdAt desc
        """,
        countQuery = """
        select count(p) from Post p
        where p.status = :status
          and p.user.id <> :userId
          and p.user.id not in (
              select b.blocked.id from UserBlock b where b.blocker.id = :userId
          )
          and p.user.id not in (
              select b.blocker.id from UserBlock b where b.blocked.id = :userId
          )
          and exists (
              select 1 from Post m
              where m.user.id = :userId
                and m.status = :status
                and m.discardCourse.id in (
                    select pw.course.id from PostWantedCourse pw where pw.post = p
                )
                and p.discardCourse.id in (
                    select mw.course.id from PostWantedCourse mw where mw.post = m
                )
                and not exists (
                    select 1 from MatchIgnore mi
                    where (mi.postA.id = m.id and mi.postB.id = p.id)
                       or (mi.postA.id = p.id and mi.postB.id = m.id)
                )
          )
        """)
    Page<Post> findRecommendedFeed(@Param("status") PostStatus status,
                                   @Param("userId") Long userId,
                                   Pageable pageable);
    // [추천 매칭] 내 게시글 A(aPostId, aDiscardCourseId) 기준 양방향 매칭 후보 조회.
    //  - B.버릴과목 ∈ A.원하는과목  → 매칭순위(aw.priority)
    //  - A.버릴과목 ∈ B.원하는과목  → 양방향 성립
    //  - 제외: 본인 글 / 비MATCHABLE / 차단(양방향) / match_ignores 동일 쌍
    @Query("""
            select new com.sookmyung.swapclass.domain.match.dto.MatchCandidateDto(
                       b.id, aw.priority, b.createdAt)
            from Post b
            join PostWantedCourse aw
              on aw.post.id = :aPostId and aw.course.id = b.discardCourse.id
            where b.status = com.sookmyung.swapclass.domain.post.entity.PostStatus.MATCHABLE
              and b.user.id <> :userId
              and exists (
                  select 1 from PostWantedCourse bw
                  where bw.post = b and bw.course.id = :aDiscardCourseId
              )
              and b.user.id not in (
                  select bl.blocked.id from UserBlock bl where bl.blocker.id = :userId
              )
              and b.user.id not in (
                  select bl.blocker.id from UserBlock bl where bl.blocked.id = :userId
              )
              and not exists (
                  select 1 from MatchIgnore mi
                  where (mi.postA.id = :aPostId and mi.postB.id = b.id)
                     or (mi.postA.id = b.id and mi.postB.id = :aPostId)
              )
            """)
    List<MatchCandidateDto> findBidirectionalCandidates(@Param("aPostId") Long aPostId,
                                                        @Param("aDiscardCourseId") Long aDiscardCourseId,
                                                        @Param("userId") Long userId);
}

