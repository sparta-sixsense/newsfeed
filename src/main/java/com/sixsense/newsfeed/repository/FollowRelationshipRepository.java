package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.FollowRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    @Query("SELECT fr FROM FollowRelationship fr JOIN FETCH fr.accepter WHERE fr.requester.id = :requesterId")
    List<FollowRelationship> findAllByRequesterWithAccepter(@Param("requesterId") Long requesterId);
}
