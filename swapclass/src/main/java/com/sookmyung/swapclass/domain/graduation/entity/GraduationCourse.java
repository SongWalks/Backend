package com.sookmyung.swapclass.domain.graduation.entity;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "graduation_courses",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_graduation_user_course",
                columnNames = {"user_id", "course_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GraduationCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 등록한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 등록한 졸업요건 과목 (강의 카탈로그 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 이수 완료 여부
    @Column(nullable = false)
    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public GraduationCourse(User user, Course course) {
        this.user = user;
        this.course = course;
        this.completed = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 이수 완료 토글 (true <-> false 반복)
    public void toggleCompleted() {
        this.completed = !this.completed;
    }
}
