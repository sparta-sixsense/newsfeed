package com.sixsense.newsfeed.service;
import com.sixsense.newsfeed.domain.FollowRelationship;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.FollowRequestDto;
import com.sixsense.newsfeed.error.exception.UserNotFoundException;
import com.sixsense.newsfeed.error.exception.base.InvalidValueException;
import com.sixsense.newsfeed.repository.FollowRelationshipRepository;
import com.sixsense.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FollowRelationshipService {

    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserRepository userRepository;

    // 팔로우 생성 ( following 기능 )
    public void createFollow(Long userId, FollowRequestDto requestDto) {
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


    // 팔로잉 목록 조회 (내가 팔로우 하는 목록)


    // 팔로우 삭제 (unfollow)
}


