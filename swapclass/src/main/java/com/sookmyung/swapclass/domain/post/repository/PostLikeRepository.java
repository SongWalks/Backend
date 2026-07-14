package com.sookmyung.swapclass.domain.post.repository;

import com.sookmyung.swapclass.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    // [내 찜 목록] 최신순
    List<PostLike> findByUserIdOrderByCreatedAtDesc(Long userId);
}
