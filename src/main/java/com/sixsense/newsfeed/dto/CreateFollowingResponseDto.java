package com.sixsense.newsfeed.dto;

import com.sixsense.newsfeed.domain.FollowRelationship;

public record CreateFollowingResponseDto(
        Long followRelationshipId,
        Long requesterId,
        String requesterName,
        String requesterEmail,
        Long accepterId,
        String accepterName,
        String accepterEmail
) {
    public static CreateFollowingResponseDto fromEntity(FollowRelationship followRelationship) {
        return new CreateFollowingResponseDto(
                followRelationship.getId(),
                followRelationship.getRequester().getId(),
                followRelationship.getRequester().getName(),
                followRelationship.getRequester().getEmail(),
                followRelationship.getAccepter().getId(),
                followRelationship.getAccepter().getName(),
                followRelationship.getAccepter().getEmail()
        );
    }
}
