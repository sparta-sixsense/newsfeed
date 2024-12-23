package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAccessTokenRequestDto(
        @NotBlank(message = "refresh_token은 필수값입니다") String refreshToken
) {
}
