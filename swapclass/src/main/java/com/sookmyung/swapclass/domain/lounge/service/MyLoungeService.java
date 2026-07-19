package com.sookmyung.swapclass.domain.lounge.service;

import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostListResponse;
import com.sookmyung.swapclass.domain.lounge.entity.LoungeBookmark;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeBookmarkRepository;
import com.sookmyung.swapclass.domain.lounge.repository.LoungePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyLoungeService {

    private final LoungePostRepository loungePostRepository;
    private final LoungeBookmarkRepository loungeBookmarkRepository;

    // [내 라운지 게시글] 내가 작성한 라운지 글 목록 (최신순)
    public LoungePostListResponse getMyPosts(Long userId) {
        return LoungePostListResponse.from(
                loungePostRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    // [북마크 목록] 내가 북마크한 라운지 글 목록 (북마크 최신순)
    public LoungePostListResponse getMyBookmarks(Long userId) {
        List<LoungePost> posts = loungeBookmarkRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(LoungeBookmark::getPost)
                .toList();
        return LoungePostListResponse.from(posts);
    }
}
