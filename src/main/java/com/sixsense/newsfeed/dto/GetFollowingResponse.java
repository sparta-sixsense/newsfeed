package com.sixsense.newsfeed.dto;


import com.sixsense.newsfeed.domain.FollowRelation;

public record GetFollowingResponse(
        Long id,
        Long followeeId,
        String followeeName,
        String followeeEmail,
        String followeeImgUrl,
        String followeeAddress,
        Integer followeeAge
) {
    public GetFollowingResponse(FollowRelation followRelation) {
        this(
                followRelation.getId(),
                followRelation.getAccepter().getId(),
                followRelation.getAccepter().getName(),
                followRelation.getAccepter().getEmail(),
                followRelation.getAccepter().getProfileImgUrl(),
                followRelation.getAccepter().getAddress(),
                followRelation.getAccepter().getAge()
        );
    }
}
