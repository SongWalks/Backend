package com.sookmyung.swapclass.domain.lounge.service;

import com.sookmyung.swapclass.domain.lounge.dto.response.LoungeLikeToggleResponse;
import com.sookmyung.swapclass.domain.lounge.entity.LoungeLike;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeLikeRepository;
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
public class LoungeLikeService {

    private final LoungeLikeRepository likeRepository;
    private final LoungePostRepository postRepository;
    private final UserRepository userRepository;

    // [좋아요 토글] POST /api/lounge/posts/{postId}/likes
    @Transactional
    public LoungeLikeToggleResponse toggle(Long userId, Long postId) {
        LoungePost post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOUNGE_POST_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<LoungeLike> existing = likeRepository.findByPostAndUser(post, user);
        boolean liked;
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            post.decreaseLikeCount();
            liked = false;
        } else {
            likeRepository.save(LoungeLike.builder().post(post).user(user).build());
            post.increaseLikeCount();
            liked = true;
        }

        return new LoungeLikeToggleResponse(liked, post.getLikeCount());
    }
}
