package com.sookmyung.swapclass.domain.graduation.dto.request;

import jakarta.validation.constraints.NotNull;

// 졸업요건 과목 등록 요청
public record GraduationCourseCreateRequest(
        @NotNull Long courseId
) {}
