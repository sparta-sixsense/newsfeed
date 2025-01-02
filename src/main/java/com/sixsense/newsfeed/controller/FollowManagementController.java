package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.CreateFollowingResponseDto;
import com.sixsense.newsfeed.dto.GetFollowingResponseDto;
import com.sixsense.newsfeed.service.FollowManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;

@RestController
@RequiredArgsConstructor
public class FollowManagementController {

    private final FollowManagementService followManagementService;

    // 팔로잉 신청
    @PostMapping("/api/users/{requesterId}/followings/{accepterId}")
    public ResponseEntity<CreateFollowingResponseDto> followUser(@PathVariable Long requesterId,
                                                                 @PathVariable Long accepterId,
                                                                 @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {

        CreateFollowingResponseDto responseDto = followManagementService.follow(accessToken, requesterId, accepterId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    /*
     * following: 팔로우를 하고 있는 행위 자체
     * followee: 팔로우 당한 사람(특정 사람)
     * 하지만 팔로한 사람들(목록)을 명시할 때는 followings
     * --> 어렵다. 그냥 친구로 하면 안 될까...
     */

    // 특정 유저가 팔로우하고 있는 팔로워 목록 가져오기
    @GetMapping("/api/users/{requesterId}/followings")
    public ResponseEntity<List<GetFollowingResponseDto>> getFollowingUsers(@PathVariable Long requesterId) {

        List<GetFollowingResponseDto> followings = followManagementService.getFollowings(requesterId);
        return ResponseEntity.ok()
                .body(followings);
    }

    /*
     * 팔로잉 취소 (사실 팔로잉 관계 테이블 자체 ID를 넘겨주면 이렇게 수고스럽게 할 필요가 없다...)
     * 그러나 그렇게 하려면 api url을 다시 생각해야 한다.
     */
    @DeleteMapping("/api/users/{requesterId}/followings/{accepterId}")
    public ResponseEntity<Void> deleteFollowing(@PathVariable Long requesterId,
                                                @PathVariable Long accepterId,
                                                @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {

        followManagementService.deleteFollowing(accessToken, requesterId, accepterId);
        return ResponseEntity.ok()
                .build();
    }
}
