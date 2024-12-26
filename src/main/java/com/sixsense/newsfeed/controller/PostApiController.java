package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.CreatePostRequestDto;
import com.sixsense.newsfeed.dto.PostResponseDto;
import com.sixsense.newsfeed.dto.UpdatePostRequestDto;
import com.sixsense.newsfeed.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping("/api/feeds")
@Tag(name = "Post Management", description = "게시글 관련 API")
public class PostApiController {
    private final PostService postService;


    @Operation(
            summary = "게시글 작성",
            description = "accessToken의 userId값을 비교해 게시글을 생성합니다.",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "201", description = "성공적으로 게시글 작성", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody CreatePostRequestDto dto,
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        PostResponseDto responseDto = postService.createPost(dto, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "자신이 작성한 게시글 조회",
            description = "accessToken의 userId값을 비교해 자신이 작성한 게시글을 최근 업데이트순 으로 조회합니다.",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 게시글 조회", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostResponseDto>> findAllMyPosts(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PostResponseDto> allMyPosts = postService.findAllMyPosts(accessToken, pageable);
        return ResponseEntity.ok(allMyPosts);
    }

    @Operation(
            summary = "모든 게시글을 조회",
            description = "모든 게시글을 최근 업데이트순으로 조회합니다",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 게시글 조회", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page - 1 , size);
        Page<PostResponseDto> findAll = postService.findAll(pageable);
        return ResponseEntity.ok(findAll);
    }

    @Operation(
            summary = "게시글 상세 조회",
            description = "특정게시글을 조회합니다",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 게시글 상세 조회", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 입니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> findById(@PathVariable Long id){
        PostResponseDto postDetails = postService.findById(id);
        return new ResponseEntity<>(postDetails,HttpStatus.OK);
    }

    @Operation(
            summary = "팔로잉 게시글 전체조회",
            description = "팔로잉 게시글을 최근 업데이트순으로 조회합니다",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 팔로잉 게시글 전체 조회", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @GetMapping("/following-posts")
    public ResponseEntity<Page<PostResponseDto>> findAllFollowingPosts(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // PageRequest 설정 (0-based index)
        Page<PostResponseDto> followingPosts = postService.findAllFollowingPosts(accessToken, pageable);
        return ResponseEntity.ok(followingPosts);
    }


    @Operation(
            summary = "게시글 수정",
            description = "팔로잉 게시글을 최근 업데이트순으로 조회합니다",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 게시글 수정", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @ApiResponse(responseCode = "403", description = "접근할 수 없는 게시글 입니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 입니다.")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> Update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequestDto requestDto,
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken
    ){
        PostResponseDto update = postService.update(id, requestDto, accessToken);
        return ResponseEntity.ok(update);
    }

    @Operation(
            summary = "게시글 삭제",
            description = "accessToken의 userId값을 비교해 게시글을 삭제합니다. ",
            tags = {"Post Management"}
    )
    @ApiResponse(responseCode = "204", description = "성공적으로 게시글 삭제", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 입니다.")
    @ApiResponse(responseCode = "401", description = "존재하지 않는 토큰 입니다.")
    @ApiResponse(responseCode = "403", description = "접근할 수 없는 게시글 입니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 입니다.")
    @ApiResponse(responseCode = "409", description = "이미 삭제된 게시글 입니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken
    ){
        postService.delete(id,accessToken);
        return ResponseEntity.noContent().build();
    }
}
