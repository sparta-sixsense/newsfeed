package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.FollowRequestDto;
import com.sixsense.newsfeed.dto.FollowResponseDto;
import com.sixsense.newsfeed.service.FollowRelationshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;


@RestController
@RequestMapping("/users/follows")
@RequiredArgsConstructor
@Tag(name = "Follow Relationship API", description = "팔로우 관련 CRUD 기능 제공")
public class FollowRelationshipController {

    private final FollowRelationshipService followRelationshipService;

    // 팔로우 생성 API
    @Operation(summary = "팔로우 하기", description = "특정 사용자가 다른 친구(사용자)를 팔로우합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "팔로우 하였습니다!"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 자기 자신을 팔로우 할 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "이미 팔로우한 친구입니다.")
    })
    @PostMapping
    public ResponseEntity<Void> createFollow(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                             @RequestBody
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     description = "팔로우 요청 DTO",
                                                     required = true,
                                                     content = @Content(
                                                             mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = FollowRequestDto.class))
                                             )
                                             FollowRequestDto req) {
        followRelationshipService.createFollow(req, accessToken);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 팔로워 목록 조회 API
    @Operation(summary = "팔로워 목록 조회", description = "특정 사용자를 팔로우하고 있는 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로워 목록 조회하기",
                         content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FollowResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/followers")
    public ResponseEntity<List<FollowResponseDto>> getFollowerList(
            @Parameter(description = "사용자 인증 토큰", required = true)
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        followRelationshipService.getFollowerList(accessToken);

        List<FollowResponseDto> followerList = followRelationshipService.getFollowerList(accessToken);
        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }

    // 팔로잉 목록 조회 API
    @Operation(summary = "팔로잉 목록 조회", description = "특정 사용자가 팔로우하고 있는 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회하기",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = FollowResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/followings")
    public ResponseEntity<List<FollowResponseDto>> getFollowingList(
            @Parameter(description = "사용자 인증 토큰", required = true)
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        followRelationshipService.getFollowingList(accessToken);

        List<FollowResponseDto> followingList = followRelationshipService.getFollowingList(accessToken);
        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    // 팔로우 삭제 (언팔로우) API
    @Operation(summary = "팔로우 삭제(언팔로우)", description = "특정 사용자의 팔로우 관계를 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "언팔로우 되었습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 관계입니다."),
            @ApiResponse(responseCode = "409", description = "이미 언팔로우한 친구입니다.")
    })
    @DeleteMapping("/{friend_id}")
    public ResponseEntity<Void> unfollow(@PathVariable("friend_id") Long friendId,
                                         @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {

        followRelationshipService.deleteFollow(friendId, accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
