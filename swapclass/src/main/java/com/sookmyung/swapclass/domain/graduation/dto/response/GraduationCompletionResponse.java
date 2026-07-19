package com.sookmyung.swapclass.domain.graduation.dto.response;

// 졸업요건 이수 완료 토글 결과 응답
public record GraduationCompletionResponse(
        boolean completed
) {
    public static GraduationCompletionResponse of(boolean completed) {
        return new GraduationCompletionResponse(completed);
    }
}
