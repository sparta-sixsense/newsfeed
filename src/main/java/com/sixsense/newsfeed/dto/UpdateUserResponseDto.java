package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.User;

public record UpdateUserResponseDto(
        Long userId,
        String name,
        String email,
        String profileImgUrl,
        Integer age,
        String address
) {
    public static UpdateUserResponseDto fromEntity(User user) {
        return new UpdateUserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImgUrl(),
                user.getAge(),
                user.getAddress()
        );
    }
}
