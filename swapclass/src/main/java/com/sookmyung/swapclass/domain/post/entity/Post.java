package com.sookmyung.swapclass.domain.post.entity;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 버릴 과목 (등록 후 수정 불가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discard_course_id", nullable = false, updatable = false)
    private Course discardCourse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    // 오픈채팅 링크 (선택)
    @Column(name = "kakao_link")
    private String kakaoLink;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 원하는 과목 1~3순위
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostWantedCourse> wantedCourses = new ArrayList<>();

    @Builder
    public Post(User user, Course discardCourse, String kakaoLink) {
        this.user = user;
        this.discardCourse = discardCourse;
        this.kakaoLink = kakaoLink;
        this.status = PostStatus.MATCHABLE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 원하는 과목 추가 (연관관계 편의 메서드)
    public void addWantedCourse(PostWantedCourse wantedCourse) {
        this.wantedCourses.add(wantedCourse);
        wantedCourse.assignPost(this);
    }

    // 수정: 원하는 과목 목록 전체 교체 (버릴 과목은 불변)
    public void replaceWantedCourses(List<PostWantedCourse> newWantedCourses) {
        this.wantedCourses.clear();
        for (PostWantedCourse wantedCourse : newWantedCourses) {
            addWantedCourse(wantedCourse);
        }
    }

    public void updateKakaoLink(String kakaoLink) {
        this.kakaoLink = kakaoLink;
    }

    // soft delete
    public void softDelete() {
        this.status = PostStatus.DELETED;
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isMatchable() {
        return this.status == PostStatus.MATCHABLE;
    }
}
