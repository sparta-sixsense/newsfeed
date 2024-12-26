package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 ID로 게시글 조회 + 페이징
    @Query("SELECT p FROM Post p " +
            "WHERE p.user.id = :userId AND p.isDeleted = false " +
            "ORDER BY p.updatedAt DESC")
    Page<Post> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    // 모든 게시글 조회 + 페이징
    Page<Post> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    // 팔로우 하고있는 사람들 게시글 조회
    @Query("SELECT p FROM Post p " +
            "JOIN FollowRelationship fr ON p.user.id = fr.following.id " +
            "WHERE fr.follower.id = :userId " +
            "AND p.isDeleted = false ORDER BY p.updatedAt DESC"
    )
    Page<Post> findPostsByUsersFollowedBy(@Param("userId") Long userId, Pageable pageable);

}
