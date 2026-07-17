package com.sookmyung.swapclass.domain.lounge.repository;

import com.sookmyung.swapclass.domain.lounge.entity.LoungeBookmark;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoungeBookmarkRepository extends JpaRepository<LoungeBookmark, Long> {

    Optional<LoungeBookmark> findByPostAndUser(LoungePost post, User user);

    boolean existsByPostAndUser(LoungePost post, User user);

    void deleteAllByPost(LoungePost post);
}
