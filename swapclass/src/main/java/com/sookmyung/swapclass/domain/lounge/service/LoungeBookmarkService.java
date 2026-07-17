package com.sookmyung.swapclass.domain.lounge.service;

import com.sookmyung.swapclass.domain.lounge.dto.response.LoungeBookmarkToggleResponse;
import com.sookmyung.swapclass.domain.lounge.entity.LoungeBookmark;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeBookmarkRepository;
import com.sookmyung.swapclass.domain.lounge.repository.LoungePostRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoungeBookmarkService {

    private final LoungeBookmarkRepository bookmarkRepository;
    private final LoungePostRepository postRepository;
    private final UserRepository userRepository;

    // [북마크 토글] POST /api/lounge/posts/{postId}/bookmarks
    @Transactional
    public LoungeBookmarkToggleResponse toggle(Long userId, Long postId) {
        LoungePost post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOUNGE_POST_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<LoungeBookmark> existing = bookmarkRepository.findByPostAndUser(post, user);
        boolean bookmarked;
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            bookmarked = false;
        } else {
            bookmarkRepository.save(LoungeBookmark.builder().post(post).user(user).build());
            bookmarked = true;
        }

        return new LoungeBookmarkToggleResponse(bookmarked);
    }
}
