package com.sookmyung.swapclass.domain.post.service;

import com.sookmyung.swapclass.domain.course.entity.Course;
import com.sookmyung.swapclass.domain.course.repository.CourseRepository;
import com.sookmyung.swapclass.domain.post.dto.request.PostCreateRequest;
import com.sookmyung.swapclass.domain.post.dto.response.PostCreateResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;
import com.sookmyung.swapclass.domain.post.repository.PostRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
