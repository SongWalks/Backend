package com.sookmyung.swapclass.domain.lounge.service;

import com.sookmyung.swapclass.domain.lounge.dto.request.LoungeCommentCreateRequest;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungeCommentCreateResponse;
import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeCommentRepository;
import com.sookmyung.swapclass.domain.lounge.repository.LoungePostRepository;
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
public class LoungeCommentService {

    private final LoungeCommentRepository commentRepository;
    private final LoungePostRepository postRepository;
    private final UserRepository userRepository;

    // [댓글 작성] POST /api/lounge/posts/{postId}/comments
    @Transactional
    public LoungeCommentCreateResponse create(Long userId, Long postId, LoungeCommentCreateRequest request) {
        LoungePost post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOUNGE_POST_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LoungeComment comment = commentRepository.save(
                LoungeComment.builder().post(post).user(user).content(request.content()).build()
        );
        post.increaseCommentCount();

        return LoungeCommentCreateResponse.from(comment);
    }

    // [댓글 삭제] DELETE /api/lounge/comments/{commentId} — 본인 댓글만
    @Transactional
    public void delete(Long userId, Long commentId) {
        LoungeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOUNGE_COMMENT_NOT_FOUND));
        if (!comment.isAuthor(userId)) {
            throw new CustomException(ErrorCode.LOUNGE_NOT_AUTHOR);
        }

        LoungePost post = comment.getPost();
        commentRepository.delete(comment);
        post.decreaseCommentCount();
    }
}
