package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.FollowRequestDto;
import com.sixsense.newsfeed.dto.FollowResponseDto;
import com.sixsense.newsfeed.service.FollowRelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;


@RestController
@RequestMapping("/users/follows")
@RequiredArgsConstructor
public class FollowRelationshipController {

    private final FollowRelationshipService followRelationshipService;

    // 팔로우 생성 API
    @PostMapping
    public ResponseEntity<Void> createFollow(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                             @RequestBody FollowRequestDto req) {
        followRelationshipService.createFollow(req, accessToken);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 팔로워 목록 조회 API
    @GetMapping("/followers")
    public ResponseEntity<List<FollowResponseDto>> getFollowerList(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        followRelationshipService.getFollowerList(accessToken);

        List<FollowResponseDto> followerList = followRelationshipService.getFollowerList(accessToken);
        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }

    // 팔로잉 목록 조회 API
    @GetMapping("/followings")
    public ResponseEntity<List<FollowResponseDto>> getFollowingList(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        followRelationshipService.getFollowingList(accessToken);

        List<FollowResponseDto> followingList = followRelationshipService.getFollowingList(accessToken);
        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    // 팔로우 삭제 (언팔로우) API
    @DeleteMapping("/{friend_id}")
    public ResponseEntity<Void> unfollow(@PathVariable("friend_id") Long friendId,
                                         @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {

        followRelationshipService.deleteFollow(friendId, accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
