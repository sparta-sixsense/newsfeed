package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UpdatePostRequestDto{

    @NotEmpty(message = "내용을 입력하세요")
    private final String content;
    private final String imgUrl;
    public UpdatePostRequestDto(String content, String imgUrl) {
        this.content = content;
        this.imgUrl = imgUrl;
    }
}
