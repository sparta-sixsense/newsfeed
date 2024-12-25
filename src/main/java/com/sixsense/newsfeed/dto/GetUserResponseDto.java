package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.User;

import java.time.LocalDateTime;

public record GetUserResponseDto(
        Long userId,
        String name,
        String address,
        Integer age,
        LocalDateTime createdAt
) {

    public static GetUserResponseDto fromEntity(User user) {
        return new GetUserResponseDto(
                user.getId(),
                user.getName(),
                user.getAddress(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
