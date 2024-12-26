package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.FollowRelationship;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    // 나를 팛로우 하고 있는 친구의 목록
    List<FollowRelationship> findAllByFollowingIdAndStatus(Long friendId, Status active);

    // 내가 팔로잉 하고 있는 친구의 목록
    List<FollowRelationship> findAllByFollowerIdAndStatus(Long userId, Status active);

    // 팔로우 삭제
    Optional<FollowRelationship> findByFollowerAndFollowing(User follower, User following);

}
