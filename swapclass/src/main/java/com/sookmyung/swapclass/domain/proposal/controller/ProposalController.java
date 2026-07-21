package com.sookmyung.swapclass.domain.proposal.controller;

import com.sookmyung.swapclass.domain.proposal.dto.request.ProposalCreateRequest;
import com.sookmyung.swapclass.domain.proposal.dto.response.CandidatePostResponse;
import com.sookmyung.swapclass.domain.proposal.dto.response.ProposalCreateResponse;
import com.sookmyung.swapclass.domain.proposal.dto.response.ProposalSummaryResponse;
import com.sookmyung.swapclass.domain.proposal.service.ProposalService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    // 교환 제안 보내기
    @PostMapping("/api/proposals")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProposalCreateResponse> createProposal(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProposalCreateRequest request
    ) {
        ProposalCreateResponse response = proposalService.createProposal(userId, request);
        return ApiResponse.success(response, "교환 요청을 보냈습니다.");
    }

    // 보낸 제안 조회 (동시 최대 1개, 없으면 data=null)
    @GetMapping("/api/proposals/sent")
    public ApiResponse<ProposalSummaryResponse> getSentProposal(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(proposalService.getSentProposal(userId));
    }

    // 교환 제안 철회
    @DeleteMapping("/api/proposals/{proposalId}")
    public ApiResponse<Void> withdrawProposal(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long proposalId
    ) {
        proposalService.withdrawProposal(userId, proposalId);
        return ApiResponse.success(null, "교환 요청을 철회했습니다.");
    }

    // 제안 가능한 내 게시글 목록 (상대 게시글 기준)
    @GetMapping("/api/posts/{postId}/candidates")
    public ApiResponse<List<CandidatePostResponse>> getCandidates(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(proposalService.getCandidates(userId, postId));
    }
}
