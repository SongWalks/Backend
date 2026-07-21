package com.sookmyung.swapclass.domain.proposal.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 교환 제안 보내기 요청.
 * senderPostId: 내 게시글, receiverPostId: 제안 대상(상대) 게시글.
 */
public record ProposalCreateRequest(
        @NotNull Long senderPostId,
        @NotNull Long receiverPostId
) {
}
