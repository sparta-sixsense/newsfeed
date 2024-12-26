package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.Post;
import com.sixsense.newsfeed.domain.User;
import jakarta.validation.constraints.NotEmpty;

public record CreatePostRequestDto(
        @NotEmpty(message = "내용을 입력하세요") String content,
        String imgUrl
) {


    public Post toEntity(User user){
        return new Post(content, imgUrl, user);
    }
}