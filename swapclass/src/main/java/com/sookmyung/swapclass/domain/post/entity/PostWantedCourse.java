package com.sookmyung.swapclass.domain.post.entity;

import com.sookmyung.swapclass.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "post_wanted_courses",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_priority",
                columnNames = {"post_id", "priority"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostWantedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 1~3 순위
    @Column(nullable = false)
    private int priority;

    @Builder
    public PostWantedCourse(Course course, int priority) {
        this.course = course;
        this.priority = priority;
    }

    // 연관관계 편의 메서드에서 호출
    void assignPost(Post post) {
        this.post = post;
    }
}
