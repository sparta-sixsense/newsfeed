package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.User;

public record SignUpResponseDto(
        Long userId,
        String name,
        String email,
        String profileImgUrl,
        Integer age,
        String address
) {

    public static SignUpResponseDto fromEntity(User user) {
        return new SignUpResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImgUrl(),
                user.getAge(),
                user.getAddress()
        );
    }
}
