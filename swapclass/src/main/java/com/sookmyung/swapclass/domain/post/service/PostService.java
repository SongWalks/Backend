package com.sookmyung.swapclass.domain.post.service;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import com.sookmyung.swapclass.domain.post.dto.request.PostCreateRequest;
import com.sookmyung.swapclass.domain.post.dto.request.PostUpdateRequest;
import com.sookmyung.swapclass.domain.post.dto.response.PostCreateResponse;
import com.sookmyung.swapclass.domain.post.dto.response.MyPostResponse;
import com.sookmyung.swapclass.domain.post.dto.response.PostDetailResponse;
import com.sookmyung.swapclass.domain.post.dto.response.PostFeedResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;
import com.sookmyung.swapclass.domain.post.repository.PostRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    @Transactional
    public PostCreateResponse createPost(Long userId, PostCreateRequest request) {

        //userId로 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //검증: 원하는 과목이 중복되지 않는지, 버릴 과목을 원하는 과목에 넣었는지를 체크함.
        validateWantedCourses(request.discardCourseId(), request.wantedCourseIds());

        //discardcourse 조회하고 post빌더로 생성함.
        Course discardCourse = findCourse(request.discardCourseId());

        Post post = Post.builder()
                .user(user)
                .discardCourse(discardCourse)
                .kakaoLink(request.kakaoLink())
                .build();

        // wantedcourseIds 순회하면서 postwantedcourse 만들고, 리스트 순서 = 우선순위(1순위부터)
        List<Long> wantedCourseIds = request.wantedCourseIds();
        for (int i = 0; i < wantedCourseIds.size(); i++) {
            Course wantedCourse = findCourse(wantedCourseIds.get(i));
            PostWantedCourse wanted = PostWantedCourse.builder()
                    .course(wantedCourse)
                    .priority(i + 1)
                    .build();
            post.addWantedCourse(wanted); // cascade 로 함께 저장됨
        }

        //응답 반환
        Post saved = postRepository.save(post);
        return PostCreateResponse.from(saved);
    }

    // 게시글 상세 조회
    // @Transactional(readOnly) 안에서 DTO 변환 → LAZY(user·discardCourse·wantedCourses) 로딩 가능
    public PostDetailResponse getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // soft delete 된 글은 없는 것으로 취급 (명세: 삭제/롤백 글은 404)
        if (post.getStatus() == PostStatus.DELETED) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        return PostDetailResponse.of(post, currentUserId);
    }

    // 게시글 피드 (매칭 전, 본인 글 제외, 학과 필터(선택), 최신순, 오프셋 페이징)
    public PageResponse<PostFeedResponse> getFeed(Long userId, String dept, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostFeedResponse> feed = postRepository
                .findFeed(PostStatus.MATCHABLE, userId, dept, pageable)
                .map(PostFeedResponse::from);
        return PageResponse.from(feed);
    }

    // 내 교환 게시글 목록 (status 지정 시 해당 상태만, 없으면 전체 - 삭제 제외)
    public List<MyPostResponse> getMyPosts(Long userId, PostStatus status) {
        if (status == PostStatus.DELETED) {
            throw new CustomException(ErrorCode.INVALID_INPUT); // 삭제 글은 조회 대상 아님
        }

        List<Post> posts = (status == null)
                ? postRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(userId, PostStatus.DELETED)
                : postRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);

        return posts.stream()
                .map(MyPostResponse::from)
                .toList();
    }

    // 게시글 수정 (원하는 과목 1~3순위 + kakaoLink만. 버릴 과목은 불변)
    @Transactional
    public void updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = findModifiablePost(postId, userId);

        Long discardCourseId = post.getDiscardCourse().getId();

        // 버릴 과목 변경 시도 거부 (요청에 담겨 왔고 현재 값과 다르면)
        if (request.discardCourseId() != null
                && !request.discardCourseId().equals(discardCourseId)) {
            throw new CustomException(ErrorCode.DISCARD_COURSE_NOT_MODIFIABLE);
        }

        // 원하는 과목 검증 (기존 버릴 과목 기준)
        validateWantedCourses(discardCourseId, request.wantedCourseIds());

        // 원하는 과목 전체 교체
        // uk_post_priority(post_id, priority) 위반 방지: 기존 행 DELETE를 먼저 flush 한 뒤 재삽입
        post.replaceWantedCourses(new ArrayList<>()); // 기존 것 orphan 제거
        postRepository.flush();                        // DELETE를 INSERT보다 먼저 실행
        for (PostWantedCourse wanted : buildWantedCourses(request.wantedCourseIds())) {
            post.addWantedCourse(wanted);
        }

        // 오픈채팅 링크 수정 (null 이면 링크 제거)
        post.updateKakaoLink(request.kakaoLink());
    }

    // 게시글 삭제 (매칭 전일 때만, soft delete)
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getStatus() == PostStatus.DELETED) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if (!post.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // 교환 중·완료 상태면 삭제 불가 (거래 파기 프로세스 별도)
        if (!post.isMatchable()) {
            throw new CustomException(ErrorCode.POST_NOT_DELETABLE);
        }

        post.softDelete();
    }

    // 수정 가능한 내 게시글 조회 (없음/삭제 → 404, 남의 글 → 403, 매칭 전 아님 → 400)
    private Post findModifiablePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getStatus() == PostStatus.DELETED) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if (!post.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        if (!post.isMatchable()) {
            throw new CustomException(ErrorCode.POST_NOT_MODIFIABLE);
        }
        return post;
    }

    // courseId 리스트 → priority(1부터) 부여한 PostWantedCourse 리스트
    private List<PostWantedCourse> buildWantedCourses(List<Long> wantedCourseIds) {
        List<PostWantedCourse> result = new ArrayList<>();
        for (int i = 0; i < wantedCourseIds.size(); i++) {
            Course course = findCourse(wantedCourseIds.get(i));
            result.add(PostWantedCourse.builder()
                    .course(course)
                    .priority(i + 1)
                    .build());
        }
        return result;
    }

    // 원하는 과목 자체 중복 금지 + 버릴 과목을 원하는 과목으로 등록 금지
    private void validateWantedCourses(Long discardCourseId, List<Long> wantedCourseIds) {
        Set<Long> unique = new HashSet<>(wantedCourseIds);
        if (unique.size() != wantedCourseIds.size()) {
            throw new CustomException(ErrorCode.WANTED_COURSE_DUPLICATED);
        }
        if (unique.contains(discardCourseId)) {
            throw new CustomException(ErrorCode.DISCARD_COURSE_CANNOT_BE_WANTED);
        }
    }

    private Course findCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
    }
}
