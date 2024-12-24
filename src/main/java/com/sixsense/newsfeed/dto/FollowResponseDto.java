package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FollowResponseDto(
        @NotBlank
        Long id,

        @NotNull
        String email,

        @NotNull
        String name
) {
}
