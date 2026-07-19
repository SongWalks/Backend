package com.sookmyung.swapclass.domain.lounge.service;

import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostListResponse;
import com.sookmyung.swapclass.domain.lounge.repository.LoungePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyLoungeService {

    private final LoungePostRepository loungePostRepository;

    // [내 라운지 게시글] 내가 작성한 라운지 글 목록 (최신순)
    public LoungePostListResponse getMyPosts(Long userId) {
        return LoungePostListResponse.from(
                loungePostRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }
}
