package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.FollowRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRelationRepository extends JpaRepository<FollowRelation, Long>, FollowRelationRepositoryForQuerydsl {

    @Query("SELECT fr FROM FollowRelation fr JOIN FETCH fr.accepter WHERE fr.requester.id = :requesterId")
    List<FollowRelation> findAllByRequesterWithAccepter(@Param("requesterId") Long requesterId);

//    @Query("SELECT fr FROM FollowRelation fr WHERE fr.requester.id = :requesterId AND fr.accepter.id = :accepterId")
//    Optional<FollowRelation> findByRequesterIdAndAccepterId(@Param("requesterId") Long requesterId, @Param("accepterId") Long accepterId);
}
