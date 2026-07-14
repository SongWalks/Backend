package com.sookmyung.swapclass.domain.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 게시글 수정 요청. 원하는 과목(1~3순위)과 오픈채팅 링크만 수정 가능.
 * discardCourseId 는 버릴 과목 변경 시도를 막기 위한 가드용(선택) — 보내면 현재 값과 같아야 한다.
 */
public record PostUpdateRequest(

        Long discardCourseId,

        @NotEmpty(message = "원하는 과목을 1개 이상 선택해주세요.")
        @Size(max = 3, message = "원하는 과목은 최대 3개까지 등록할 수 있습니다.")
        List<Long> wantedCourseIds,

        String kakaoLink
) {}
