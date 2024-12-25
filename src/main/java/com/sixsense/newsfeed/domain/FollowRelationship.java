package com.sixsense.newsfeed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// user_id와 friend_id는 같을 수 없음
@Table(name = "follow_relationship", uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "acceptor_id"}))
public class FollowRelationship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepter_id", nullable = false)
    private User accepter;

    public FollowRelationship(User requester, User accepter) {
        this.requester = requester;
        this.accepter = accepter;
    }
}