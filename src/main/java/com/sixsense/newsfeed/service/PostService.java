package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.Post;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.CreatePostRequestDto;
import com.sixsense.newsfeed.dto.PostResponseDto;
import com.sixsense.newsfeed.dto.UpdatePostRequestDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.AccessDeniedException;
import com.sixsense.newsfeed.error.exception.base.NotFoundException;
import com.sixsense.newsfeed.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PostRepository postRepository;

    // 게시글 생성
    public PostResponseDto createPost(CreatePostRequestDto dto, String token) {
        Long userId = tokenProvider.getUserId(token);
        User currentUser = userService.getById(userId);

        // 게시글 생성
        Post createdPost = postRepository.save(dto.toEntity(currentUser));
        return PostResponseDto.fromEntity(createdPost);
    }

    public List<PostResponseDto> findAllMyPosts(String token) {
        Long userId = tokenProvider.getUserId(token);
        return postRepository.findAllByUserId(userId)
                .stream()
                .map(PostResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> findAll() {
        return postRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(PostResponseDto::fromEntity)
                .toList();
    }

    public PostResponseDto findById(Long id) {
        Post findPost = postRepository.findByIdOrElseThrow(id);
        return new PostResponseDto(findPost.getId(),findPost.getUser().getId(),findPost.getUser().getName(),findPost.getContent(),findPost.getImgUrl(),findPost.getUpdatedAt());
    }

    public PostResponseDto update(Long id,
                                  @Valid UpdatePostRequestDto requestDto,
                                  String token) {

        Long userId = tokenProvider.getUserId(token);
//        User currentUser = userService.getById(userId);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("피드가 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("사용자가 이 게시글을 수정할 권한이 없습니다.");
        }

        post.update(requestDto.getContent(),requestDto.getImgUrl());

        postRepository.save(post);

        return new PostResponseDto(
                post.getId(),
                post.getUser().getId(),         // User ID
                post.getUser().getName(),
                post.getContent(),
                post.getImgUrl(),
                post.getUpdatedAt()
        );
    }

    public void delete(Long id, String token) {
        Long userId = tokenProvider.getUserId(token);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제 할 피드가 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("사용자가 이 게시글을 삭제할 권한이 없습니다.");
        }

        // 3. 게시글 삭제
        postRepository.delete(post);
    }

}
