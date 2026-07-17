package com.sookmyung.swapclass.domain.lounge.dto.request;

import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 과목 태그(course)는 수정 불가 → 요청 항목에서 제외
public record LoungePostUpdateRequest(
        @NotNull(message = "게시글 유형을 선택해주세요.")
        LoungePostType type,

        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {}
