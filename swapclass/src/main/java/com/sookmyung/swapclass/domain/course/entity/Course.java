package com.sookmyung.swapclass.domain.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String professor;

    @Column(name = "class_time")
    private String classTime;

    @Column(name = "course_type")
    private String courseType;

    @Column
    private String department;

    @Column
    private String category;

    @Column
    private String area;

    @Column(name = "is_graduation_req")
    private boolean isGraduationReq;

    @Builder
    public Course(String name, String professor, String classTime, String courseType,
                  String department, String category, String area, boolean isGraduationReq) {
        this.name = name;
        this.professor = professor;
        this.classTime = classTime;
        this.courseType = courseType;
        this.department = department;
        this.category = category;
        this.area = area;
        this.isGraduationReq = isGraduationReq;
    }
}