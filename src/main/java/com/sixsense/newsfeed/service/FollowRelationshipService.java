package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.FollowRelationship;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.FollowRequestDto;
import com.sixsense.newsfeed.dto.FollowResponseDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.UserNotFoundException;
import com.sixsense.newsfeed.error.exception.base.ConflictException;
import com.sixsense.newsfeed.error.exception.base.InvalidValueException;
import com.sixsense.newsfeed.error.exception.base.NotFoundException;
import com.sixsense.newsfeed.repository.FollowRelationshipRepository;
import com.sixsense.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowRelationshipService {

    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    // 팔로우 생성 ( following 기능 )
    @Transactional
    public void createFollow(FollowRequestDto requestDto, String accessToken) {
        Long userId = tokenProvider.getUserId(accessToken);

        User follower = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        User following = userRepository.findById(requestDto.friendId())
                .orElseThrow(UserNotFoundException::new);

        // 자기 자신을 팔로우 할 수 없음
        if (follower.equals(following)) {
            throw new InvalidValueException(ErrorCode.FOLLOW_RELATION_INVALID_VALUE);
        }

        Optional<FollowRelationship> existingRelation = followRelationshipRepository.findByFollowerAndFollowing(follower, following);

        // 기존 팔로우 관계가 있는지 확인
        if (existingRelation.isPresent()) {
            FollowRelationship relationship = existingRelation.get();

            // 이미 팔로우 중인 경우
            if (relationship.getStatus() == Status.ACTIVE) {
                throw new ConflictException(ErrorCode.FOLLOW_RELATION_ALREADY_ACTIVE);
            }

            // inactive -> active 변경
            relationship.active();
            followRelationshipRepository.save(relationship);
        } else {
            followRelationshipRepository.save(new FollowRelationship(follower, following));
        }
    }


    // 팔로워 목록 조회 (나를 팔로우 하는 목록)
    public List<FollowResponseDto> getFollowerList(String accessToken) {
        Long userId = tokenProvider.getUserId(accessToken);

        List<FollowRelationship> relationships = followRelationshipRepository.findAllByFollowingIdAndStatus(userId, Status.ACTIVE);
        return relationships.stream()
                .map(relationship -> new FollowResponseDto(
                        relationship.getFollower().getId(),
                        relationship.getFollower().getEmail(),
                        relationship.getFollower().getName()
                ))
                .toList();
    }

    // 팔로잉 목록 조회 (내가 팔로우 하는 목록)
    public List<FollowResponseDto> getFollowingList(String accessToken) {
        Long userId = tokenProvider.getUserId(accessToken);

        List<FollowRelationship> relationships = followRelationshipRepository.findAllByFollowerIdAndStatus(userId, Status.ACTIVE);
        return relationships.stream()
                .map(relationship -> new FollowResponseDto(
                        relationship.getFollowing().getId(),
                        relationship.getFollowing().getEmail(),
                        relationship.getFollowing().getName()
                ))
                .toList();
    }

    // 팔로우 삭제 (unfollow)
    @Transactional
    public void deleteFollow(Long friendId, String accessToken) {
        Long userId = tokenProvider.getUserId(accessToken);

        // follower: 요청한 사용자
        User follower = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // following: 언팔로우 할 대상 사용자
        User following = userRepository.findById(friendId)
                .orElseThrow(UserNotFoundException::new);

        //팔로우 관계가 존재하는지 확인
        FollowRelationship relationship = followRelationshipRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FOLLOW_RELATION_NOT_FOUND));

        relationship.inactive();

        //물리적 데이터 삭제 X, relationship_status Inactive로 변경
        followRelationshipRepository.save(relationship);
    }
}


