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
        Post findPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        return PostResponseDto.fromEntity(findPost);
    }

    public PostResponseDto update(Long id,
                                  @Valid UpdatePostRequestDto requestDto,
                                  String token) {

        Long userId = tokenProvider.getUserId(token);
//        User currentUser = userService.getById(userId);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.POST_ACCESS_DENIED);
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
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.POST_ACCESS_DENIED);
        }

        // 3. 게시글 삭제
        postRepository.delete(post);
    }

}
