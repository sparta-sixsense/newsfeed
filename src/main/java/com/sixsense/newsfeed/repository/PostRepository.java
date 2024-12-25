package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 ID로 게시글 조회 + 페이징
    @Query("SELECT p FROM Post p WHERE p.status = 'ACTIVE' ORDER BY p.updatedAt DESC")
    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    // 모든 게시글 조회 + 페이징
    Page<Post> findAllByOrderByUpdatedAtDesc(Pageable pageable);

}
