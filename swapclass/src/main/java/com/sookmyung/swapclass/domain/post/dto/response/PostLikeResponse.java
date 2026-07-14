package com.sookmyung.swapclass.domain.post.dto.response;

/**
 * 찜 추가/취소 결과. liked=true 면 찜한 상태, false 면 해제된 상태.
 * 프론트에서 하트 토글 상태를 갱신하는 데 사용.
 */
public record PostLikeResponse(Long postId, boolean liked) {
}
