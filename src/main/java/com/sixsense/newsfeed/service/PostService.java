package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.Post;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.CreatePostRequestDto;
import com.sixsense.newsfeed.dto.PostResponseDto;
import com.sixsense.newsfeed.dto.UpdatePostRequestDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.AccessDeniedException;
import com.sixsense.newsfeed.error.exception.base.ConflictException;
import com.sixsense.newsfeed.error.exception.base.NotFoundException;
import com.sixsense.newsfeed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        // 사용자 상태 확인
        if (currentUser.getStatus() == Status.INACTIVE) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        
        Post createdPost = postRepository.save(dto.toEntity(currentUser));
        return PostResponseDto.fromEntity(createdPost);
    }

    // 내가쓴 게시글 조회
    public Page<PostResponseDto> findAllMyPosts(String token, Pageable pageable) {
        Long userId = tokenProvider.getUserId(token);
        User currentUser = userService.getById(userId);

        // 사용자 상태 확인
        if (currentUser.getStatus() == Status.INACTIVE) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        return postRepository.findAllByUserId(userId, pageable)
                .map(PostResponseDto::fromEntity);
    }

    // 팔로잉 게시글 조회
    public Page<PostResponseDto> findAllFollowingPosts(String token, Pageable pageable) {
        Long userId = tokenProvider.getUserId(token);

        return postRepository.findPostsByUsersFollowedBy(userId, pageable)
                .map(PostResponseDto::fromEntity);
    }

    // 모든 게시글 조회
    public Page<PostResponseDto> findAll(Pageable pageable) {
        return postRepository.findAllByOrderByUpdatedAtDesc(pageable)
                .map(PostResponseDto::fromEntity);
    }

    // 특정 게시글 상세조회
    public PostResponseDto findById(Long id) {
        Post findPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        return PostResponseDto.fromEntity(findPost);
    }

    // 게시글 수정
    public PostResponseDto update(Long id,
                                  UpdatePostRequestDto requestDto,
                                  String token) {

        Long userId = tokenProvider.getUserId(token);
        User currentUser = userService.getById(userId);

        // 사용자 상태 확인
        if (currentUser.getStatus() == Status.INACTIVE) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if(post.isDeleted()){
            throw new ConflictException(ErrorCode.POST_ALREADY_DELETED);
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.POST_ACCESS_DENIED);
        }

        post.update(requestDto.content(),requestDto.imgUrl());

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

    // 게시글 삭제 (Soft Delete)
    public void delete(Long id, String token) {
        Long userId = tokenProvider.getUserId(token);
        User currentUser = userService.getById(userId);

        // 사용자 상태 확인
        if (currentUser.getStatus() == Status.INACTIVE) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if(post.isDeleted()){
            throw new ConflictException(ErrorCode.POST_ALREADY_DELETED);
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.POST_ACCESS_DENIED);
        }

        post.setIsDeleted(true);

        postRepository.save(post);
    }

}
