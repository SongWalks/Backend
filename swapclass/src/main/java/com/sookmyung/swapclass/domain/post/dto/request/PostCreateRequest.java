package com.sookmyung.swapclass.domain.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 게시글 작성 요청.
 * wantedCourseIds 의 순서가 곧 우선순위(1~3순위)가 된다.
 */
public record PostCreateRequest(

        @NotNull(message = "버릴 과목은 필수입니다.")
        Long discardCourseId,

        @NotEmpty(message = "원하는 과목을 1개 이상 선택해주세요.")
        @Size(max = 3, message = "원하는 과목은 최대 3개까지 등록할 수 있습니다.")
        List<Long> wantedCourseIds,

        // 오픈채팅 링크(선택)
        String kakaoLink
) {}
