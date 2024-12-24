package com.sixsense.newsfeed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record FollowRequestDto(
        @NotNull
        @JsonProperty("friend_id") // JSON 요청/응답에서 이 필드의 이름을 friend_id로 사용
        Long friendId // 팔로우 대상 친구의 id값
) {
}
