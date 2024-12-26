package com.sixsense.newsfeed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follow_relationship", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
public class FollowRelationship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // follower 팔로우를 요청한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User follower;

    // following 팔로우를 받는 대상 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User following;

    // 팔로우 관계 생성 시 기본 상태 ACTIVE
    public FollowRelationship(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    // 팔로우 상태 변경 메서드
    // boolean 소프트 딜리트 ...
}