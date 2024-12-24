package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.FollowRelationship;
import jakarta.validation.constraints.NotNull;

public record FollowResponseDto(
        @NotNull Long id,
        @NotNull String email,
        @NotNull String name
) {
    public FollowResponseDto(FollowRelationship relationship) {
        this(
                relationship.getFollowing().getId(),
                relationship.getFollowing().getEmail(),
                relationship.getFollowing().getName()
        );
    }
}
