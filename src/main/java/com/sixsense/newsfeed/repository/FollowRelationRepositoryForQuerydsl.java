package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.FollowRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FollowRelationRepositoryForQuerydsl {

    Optional<FollowRelation> findByRequesterIdAndAccepterId(Long requesterId, Long accepterId);

    Page<FollowRelation> searchSimplePage(Pageable pageable);
}
