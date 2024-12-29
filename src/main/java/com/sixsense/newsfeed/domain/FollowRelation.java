package com.sixsense.newsfeed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// user_id와 friend_id는 같을 수 없음
@Table(name = "follow_relation", uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "accepter_id"}))
public class FollowRelation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
//    @JoinColumn(name = "requester_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepter_id", nullable = false)
//    @JoinColumn(name = "accepter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User accepter;

    public FollowRelation(User requester, User accepter) {
        this.requester = requester;
        this.accepter = accepter;
    }
}