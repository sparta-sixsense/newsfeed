package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        Long userId,
        String name,
        String content,
        String imgUrl,
        LocalDateTime updatedAt

) {
    // 엔티티에서 응답 DTO로 변환하는 메서드
    public static PostResponseDto fromEntity(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getUser().getId(),         // User ID
                post.getUser().getName(),
                post.getContent(),
                post.getImgUrl(),
                post.getUpdatedAt()
        );
    }

}