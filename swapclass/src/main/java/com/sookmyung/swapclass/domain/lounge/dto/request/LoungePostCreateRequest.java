package com.sookmyung.swapclass.domain.lounge.dto.request;

import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoungePostCreateRequest(
        @NotNull(message = "게시글 유형을 선택해주세요.")
        LoungePostType type,

        @NotNull(message = "과목 태그를 선택해주세요.")
        Long courseId,

        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {}
