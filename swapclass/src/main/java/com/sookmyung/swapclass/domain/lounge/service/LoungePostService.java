package com.sookmyung.swapclass.domain.lounge.service;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import com.sookmyung.swapclass.domain.lounge.dto.request.LoungePostCreateRequest;
import com.sookmyung.swapclass.domain.lounge.dto.request.LoungePostUpdateRequest;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostCreateResponse;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostDetailResponse;
import com.sookmyung.swapclass.domain.lounge.dto.response.LoungePostListResponse;
import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeBookmarkRepository;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeCommentRepository;
import com.sookmyung.swapclass.domain.lounge.repository.LoungeLikeRepository;
import com.sookmyung.swapclass.domain.lounge.repository.LoungePostRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoungePostService {

    private final LoungePostRepository postRepository;
    private final LoungeCommentRepository commentRepository;
    private final LoungeLikeRepository likeRepository;
    private final LoungeBookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // [작성] POST /api/lounge/posts
    @Transactional
    public LoungePostCreateResponse create(Long userId, LoungePostCreateRequest request) {
        User user = findUser(userId);
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        LoungePost post = LoungePost.builder()
                .user(user)
                .course(course)
                .type(request.type())
                .title(request.title())
                .content(request.content())
                .build();

        return new LoungePostCreateResponse(postRepository.save(post).getId());
    }

    // [목록] GET /api/lounge/posts — 유형/과목태그/검색어 필터, 최신순
    public LoungePostListResponse getList(LoungePostType type, Long courseId, String keyword) {
        return LoungePostListResponse.from(postRepository.search(type, courseId, normalize(keyword)));
    }

    // [상세] GET /api/lounge/posts/{postId}
    // 비로그인 조회 허용 대비: userId가 null이면 liked/bookmarked는 false로 처리
    public LoungePostDetailResponse getDetail(Long postId, Long userId) {
        LoungePost post = findPost(postId);
        List<LoungeComment> comments = commentRepository.findAllByPostOrderByCreatedAtAsc(post);

        boolean liked = false;
        boolean bookmarked = false;
        if (userId != null) {
            User user = userRepository.getReferenceById(userId);
            liked = likeRepository.existsByPostAndUser(post, user);
            bookmarked = bookmarkRepository.existsByPostAndUser(post, user);
        }

        return LoungePostDetailResponse.of(post, liked, bookmarked, comments);
    }

    // [수정] PATCH /api/lounge/posts/{postId} — 본인 글만, 과목 태그는 수정 불가
    @Transactional
    public void update(Long userId, Long postId, LoungePostUpdateRequest request) {
        LoungePost post = findPost(postId);
        validateAuthor(post, userId);
        post.update(request.type(), request.title(), request.content());
    }

    // [삭제] DELETE /api/lounge/posts/{postId} — 본인 글만, 연관 데이터 함께 삭제(hard delete)
    @Transactional
    public void delete(Long userId, Long postId) {
        LoungePost post = findPost(postId);
        validateAuthor(post, userId);

        commentRepository.deleteAllByPost(post);
        likeRepository.deleteAllByPost(post);
        bookmarkRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private LoungePost findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOUNGE_POST_NOT_FOUND));
    }

    private void validateAuthor(LoungePost post, Long userId) {
        if (!post.isAuthor(userId)) {
            throw new CustomException(ErrorCode.LOUNGE_NOT_AUTHOR);
        }
    }

    private String normalize(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
