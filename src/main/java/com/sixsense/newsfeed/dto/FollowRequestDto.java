package com.sixsense.newsfeed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "팔로우 요청 DTO")
public record FollowRequestDto(
        @NotNull
        @Schema(description = "팔로우할 친구의 ID", example = "23")
        @JsonProperty("friend_id") // JSON 요청/응답에서 이 필드의 이름을 friend_id로 사용
        Long friendId // 팔로우 대상 친구의 id값
) {
}
