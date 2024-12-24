package com.sixsense.newsfeed.dto;

public record LoginResponseDto(
        Long UserId,
        String accessToken,
        String refreshToken
) {
}
