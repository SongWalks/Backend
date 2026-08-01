package com.sookmyung.swapclass.domain.proposal.repository;

/**
 * 게시글별 받은 제안 수 집계 프로젝션 (피드 proposalCount N+1 방지용).
 * JPQL alias(postId, count)와 getter 이름이 일치해야 인터페이스 프로젝션이 매핑된다.
 */
public interface PostProposalCount {
    Long getPostId();
    long getCount();
}
