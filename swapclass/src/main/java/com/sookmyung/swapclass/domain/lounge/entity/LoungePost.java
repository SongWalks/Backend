package com.sookmyung.swapclass.domain.lounge.entity;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lounge_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoungePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 과목 태그 (필수, 수정 불가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoungePostType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 목록 표시용 비정규화 카운트 (좋아요/댓글 시 트랜잭션으로 증감)
    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public LoungePost(User user, Course course, LoungePostType type, String title, String content) {
        this.user = user;
        this.course = course;
        this.type = type;
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.commentCount = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 과목 태그(course)는 수정 불가 → 유형/제목/내용만 변경
    public void update(LoungePostType type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public boolean isAuthor(Long userId) {
        return this.user.getId().equals(userId);
    }
}
