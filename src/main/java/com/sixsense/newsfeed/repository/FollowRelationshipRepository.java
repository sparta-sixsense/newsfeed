package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.FollowRelationship;
import com.sixsense.newsfeed.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    // 이미 팔로잉하고 있는지 확인
    boolean existsByFollowerAndFollowing(User follower, User following);

    // 나를 팛로우 하고 있는 친구의 목록


    // 내가 팔로잉 하고 있는 친구의 목록


    // 팔로우 삭제

}
