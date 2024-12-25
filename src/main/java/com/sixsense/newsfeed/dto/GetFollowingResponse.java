package com.sixsense.newsfeed.dto;


import com.sixsense.newsfeed.domain.FollowRelationship;

public record GetFollowingResponse(
        Long id,
        Long followeeId,
        String followeeName,
        String followeeEmail,
        String followeeImgUrl,
        String followeeAddress,
        Integer followeeAge
) {
    public GetFollowingResponse(FollowRelationship followRelationship) {
        this(
                followRelationship.getId(),
                followRelationship.getAccepter().getId(),
                followRelationship.getAccepter().getName(),
                followRelationship.getAccepter().getEmail(),
                followRelationship.getAccepter().getProfileImgUrl(),
                followRelationship.getAccepter().getAddress(),
                followRelationship.getAccepter().getAge()
        );
    }
}
