package com.sookmyung.swapclass.domain.proposal.dto.response;

/**
 * 교환 제안 수락 응답. 생성된 교환 채팅방 ID를 반환한다.
 */
public record ProposalAcceptResponse(
        Long chatRoomId
) {
}
