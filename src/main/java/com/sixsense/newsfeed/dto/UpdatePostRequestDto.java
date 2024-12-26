package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.NotEmpty;

public record UpdatePostRequestDto(
        @NotEmpty(message = "내용을 입력하세요") String content, String imgUrl
) {

    public UpdatePostRequestDto(String content, String imgUrl) {
        this.content = content;
        this.imgUrl = imgUrl;
    }
}
