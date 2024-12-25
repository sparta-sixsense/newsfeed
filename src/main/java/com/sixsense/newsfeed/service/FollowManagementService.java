package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.FollowRelationship;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.CreateFollowingResponseDto;
import com.sixsense.newsfeed.dto.GetFollowingResponse;
import com.sixsense.newsfeed.repository.FollowRelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowManagementService {

    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserService userService;
    private final TokenProvider tokenProvider;

    // 팔로잉 신청
    @Transactional
    public CreateFollowingResponseDto follow(Long requesterId, Long accepterId, String accessToken) {

        Long userId = tokenProvider.getUserId(accessToken);
        User requester = userService.getById(requesterId);
        User accepter = userService.getById(accepterId);

        // 유저 권한 확인
        userService.validateIsAccessible(userId, accessToken);

        // 유저 status 확인
        userService.validateIsActiveUser(requester);

        // 팔로우 신청
        FollowRelationship savedRelationship = followRelationshipRepository.save(new FollowRelationship(requester, accepter));
        return CreateFollowingResponseDto.fromEntity(savedRelationship);
    }

    // 특정 유저가 팔로잉 하고 있는 'following' 목록 가져오기
    public List<GetFollowingResponse> getFollowings(Long requesterId) {

        // requester가 Active 상태일 때만 팔로잉 목록 확인 가능
        User requester = userService.getById(requesterId);
        userService.validateIsActiveUser(requester);

        return followRelationshipRepository.findAllByRequesterWithAccepter(requesterId)
                .stream()
                .filter(fr -> fr.getAccepter().getStatus() != Status.DELETED)
                .map(GetFollowingResponse::new)
                .toList();
    }
}
