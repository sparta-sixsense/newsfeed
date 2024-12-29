package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.FollowRelation;

public record CreateFollowingResponseDto(
        Long followRelationshipId,
        Long requesterId,
        String requesterName,
        String requesterEmail,
        Long accepterId,
        String accepterName,
        String accepterEmail
) {
    public static CreateFollowingResponseDto fromEntity(FollowRelation followRelation) {
        return new CreateFollowingResponseDto(
                followRelation.getId(),
                followRelation.getRequester().getId(),
                followRelation.getRequester().getName(),
                followRelation.getRequester().getEmail(),
                followRelation.getAccepter().getId(),
                followRelation.getAccepter().getName(),
                followRelation.getAccepter().getEmail()
        );
    }
}
