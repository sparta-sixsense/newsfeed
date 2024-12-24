package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.CreatePostRequestDto;
import com.sixsense.newsfeed.dto.PostResponseDto;
import com.sixsense.newsfeed.dto.UpdatePostRequestDto;
import com.sixsense.newsfeed.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/feeds")
public class PostApiController {
    private final PostService postService;

    // 글작성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody CreatePostRequestDto dto,
            @RequestHeader("Authorization") String token) {
        PostResponseDto responseDto = postService.createPost(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<PostResponseDto>> findAllMyPosts(
            @RequestHeader("Authorization") String token) { // 토큰 추가
        List<PostResponseDto> allMyPosts = postService.findAllMyPosts(token);
        return new ResponseEntity<>(allMyPosts, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> findAll(){
        List<PostResponseDto> findAll = postService.findAll();
        return new ResponseEntity<>(findAll, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> findById(@PathVariable Long id){
        PostResponseDto postDetails = postService.findById(id);
        return new ResponseEntity<>(postDetails,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> Update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequestDto requestDto,
            @RequestHeader("Authorization") String token
    ){
        PostResponseDto update = postService.update(id, requestDto, token);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ){
        postService.delete(id,token);
        return ResponseEntity.noContent().build();
    }
}
