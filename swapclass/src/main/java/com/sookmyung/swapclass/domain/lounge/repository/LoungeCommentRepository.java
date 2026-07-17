package com.sookmyung.swapclass.domain.lounge.repository;

import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoungeCommentRepository extends JpaRepository<LoungeComment, Long> {

    List<LoungeComment> findAllByPostOrderByCreatedAtAsc(LoungePost post);

    void deleteAllByPost(LoungePost post);
}
