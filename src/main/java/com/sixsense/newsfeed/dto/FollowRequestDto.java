package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDto(
        @NotNull
        Long friend_id // 팔로우 대상 친구의 id값
) {
}
