package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.FollowRelation;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.CreateFollowingResponseDto;
import com.sixsense.newsfeed.dto.GetFollowingResponseDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.EntityAlreadyExistsException;
import com.sixsense.newsfeed.error.exception.base.NotFoundException;
import com.sixsense.newsfeed.repository.FollowRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowManagementService {

    private final FollowRelationRepository followRelationRepository;
    private final UserService userService;
    private final TokenProvider tokenProvider;

    // 팔로잉 신청
    @Transactional
    public CreateFollowingResponseDto follow(String requesterAccessToken, Long requesterId, Long accepterId) {

        Long userId = tokenProvider.getUserId(requesterAccessToken);
        User requester = userService.getById(requesterId);
        User accepter = userService.getById(accepterId);

        // 유저 권한 확인
        userService.validateIsAccessible(userId, requesterAccessToken);

        // requester, accepter status 확인
        userService.validateIsActiveUser(requester);
        userService.validateIsActiveUser(accepter);

        // 현재 팔로잉 중인지 확인
        if (isFollowingNow(requesterId, accepterId)) {
            throw new EntityAlreadyExistsException(ErrorCode.FOLLOWING_ALREADY_EXISTS);
        }

        // 팔로우 신청
        FollowRelation savedRelationship = followRelationRepository.save(new FollowRelation(requester, accepter));
        return CreateFollowingResponseDto.fromEntity(savedRelationship);
    }

    // 특정 유저가 팔로잉 하고 있는 'following' 목록 가져오기
    public List<GetFollowingResponseDto> getFollowings(Long requesterId) {

        // requester가 Active 상태일 때만 팔로잉 목록 확인 가능
        User requester = userService.getById(requesterId);
        userService.validateIsActiveUser(requester);

        return followRelationRepository.findAllByRequesterWithAccepter(requesterId)
                .stream()
                .filter(fr -> fr.getAccepter().getStatus() != Status.DELETED)
                .map(GetFollowingResponseDto::new)
                .toList();
    }

    // 팔로잉 관계 삭제
    @Transactional
    public void deleteFollowing(String accessToken, Long requesterId, Long accepterId) {

        Long userId = tokenProvider.getUserId(accessToken);
        User requester = userService.getById(requesterId);
        User accepter = userService.getById(accepterId);

        // 유저 권한 확인
        userService.validateIsAccessible(userId, accessToken);

        // 유저 status 확인
        userService.validateIsActiveUser(requester);

        FollowRelation findFollowRelation = followRelationRepository.findByRequesterIdAndAccepterId(requester.getId(), accepter.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.FOLLOWING_NOT_FOUND));

        // 팔로잉 관계 삭제
        followRelationRepository.delete(findFollowRelation);
    }

    // 현재 팔로잉 중인지 확인
    private boolean isFollowingNow(Long requesterId, Long accepterId) {
        return followRelationRepository.findByRequesterIdAndAccepterId(requesterId, accepterId)
                .isPresent();
    }
}
