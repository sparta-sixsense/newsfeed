package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.FollowRelationship;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "팔로우 응답 DTO")
public record FollowResponseDto(
        @Schema(description = "사용자 ID", example = "23") @NotNull Long id,
        @Schema(description = "사용자 이메일", example = "test123@example.com") @NotNull String email,
        @Schema(description = "사용자 이름", example = "두루미") @NotNull String name
) {
    public FollowResponseDto(FollowRelationship relationship) {
        this(
                relationship.getFollowing().getId(),
                relationship.getFollowing().getEmail(),
                relationship.getFollowing().getName()
        );
    }
}
