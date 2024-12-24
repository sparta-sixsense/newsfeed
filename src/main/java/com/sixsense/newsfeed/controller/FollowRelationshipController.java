package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.service.FollowRelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users/{user_id}/follows")
@RequiredArgsConstructor
public class FollowRelationshipController {

    private final FollowRelationshipService followRelationshipService;

    // 팔로우 생성 API


    // 팔로워 목록 조회 API


    // 팔로잉 목록 조회 API

    // 팔로우 삭제 (언팔로우) API


}
