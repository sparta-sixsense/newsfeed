package com.sixsense.newsfeed.dto;

public record LoginResponseDto(
        Long userId,
        String accessToken,
        String refreshToken
) {
}
