package com.sookmyung.swapclass.domain.post.service;

import com.sookmyung.swapclass.domain.post.dto.response.PostLikeResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostLike;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.repository.PostLikeRepository;
import com.sookmyung.swapclass.domain.post.repository.PostRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 찜하기 (멱등: 이미 찜했으면 아무 일 없이 liked=true 반환)
    @Transactional
    public PostLikeResponse addLike(Long userId, Long postId) {
        Post post = findActivePost(postId);

        if (!postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            postLikeRepository.save(
                    PostLike.builder().user(user).post(post).build()
            );
        }
        return new PostLikeResponse(postId, true);
    }

    // 찜 취소 (멱등: 찜 안 한 상태여도 그냥 liked=false 반환)
    @Transactional
    public PostLikeResponse removeLike(Long userId, Long postId) {
        postLikeRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(postLikeRepository::delete);
        return new PostLikeResponse(postId, false);
    }

    // 삭제된 글은 찜 대상에서 제외
    private Post findActivePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (post.getStatus() == PostStatus.DELETED) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        return post;
    }
}
