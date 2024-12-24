package com.sixsense.newsfeed.service;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.FollowRelationship;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.FollowRequestDto;
import com.sixsense.newsfeed.dto.FollowResponseDto;
import com.sixsense.newsfeed.error.exception.UserNotFoundException;
import com.sixsense.newsfeed.error.exception.base.InvalidValueException;
import com.sixsense.newsfeed.repository.FollowRelationshipRepository;
import com.sixsense.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO 팔로우 관게 예외를 따로 만들기!
 */

@Service
@RequiredArgsConstructor
public class FollowRelationshipService {

    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    // 팔로우 생성 ( following 기능 )
    public void createFollow(Long userId, FollowRequestDto requestDto, String accessToken) {
        Long tokenId = tokenProvider.getUserId(accessToken);

        User follower = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        User following = userRepository.findById(requestDto.friendId())
                .orElseThrow(UserNotFoundException::new);


        if (follower.equals(following)) {
            throw new InvalidValueException(); // 잘못된 요청입니다. 자기 자신을 팔로우할 수 없습니다.
        }

        boolean alreadyFollowing = followRelationshipRepository.existsByFollowerAndFollowing(follower, following);
        if (alreadyFollowing) {
            throw new InvalidValueException(); // 이미 팔로우하는 친구입니다.
        }

        followRelationshipRepository.save(new FollowRelationship(follower, following));
    }

    // 팔로워 목록 조회 (나를 팔로우 하는 목록)
    public List<FollowResponseDto> getFollowerList(Long userId, String accessToken) {
        Long tokenId = tokenProvider.getUserId(accessToken);

        List<FollowRelationship> relationships = followRelationshipRepository.findAllByFollowingId(userId);
        return relationships.stream()
                .map(relationship -> new FollowResponseDto(
                        relationship.getFollower().getId(),
                        relationship.getFollower().getEmail(),
                        relationship.getFollower().getName()
                ))
                .toList();
    }

    // 팔로잉 목록 조회 (내가 팔로우 하는 목록)


    // 팔로우 삭제 (unfollow)
}


