package com.sookmyung.swapclass.domain.match.entity;

import com.sookmyung.swapclass.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 거절/무산으로 인해 동일 게시글 쌍이 다시 추천되지 않도록 제외하는 블랙리스트.
 * 쌍은 순서를 정규화해 저장한다(작은 postId를 postA로). → 조회 시 방향 무관하게 1건으로 조회.
 */
@Entity
@Table(
        name = "match_ignores",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_match_ignore_pair",
                columnNames = {"post_a_id", "post_b_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchIgnore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 정규화된 쌍 중 작은 id의 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_a_id", nullable = false)
    private Post postA;

    // 정규화된 쌍 중 큰 id의 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_b_id", nullable = false)
    private Post postB;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private MatchIgnore(Post postA, Post postB) {
        this.postA = postA;
        this.postB = postB;
    }

    // 두 게시글의 순서를 정규화해 생성 (작은 id → postA)
    public static MatchIgnore of(Post post1, Post post2) {
        if (post1.getId() <= post2.getId()) {
            return new MatchIgnore(post1, post2);
        }
        return new MatchIgnore(post2, post1);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
