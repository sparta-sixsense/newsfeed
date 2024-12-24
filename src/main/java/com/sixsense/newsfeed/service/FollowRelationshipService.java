package com.sixsense.newsfeed.service;
import com.sixsense.newsfeed.repository.FollowRelationshipRepository;
import com.sixsense.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FollowRelationshipService {

    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserRepository userRepository;

    // 팔로우 생성 ( following 기능 )


    // 팔로워 목록 조회 (나를 팔로우 하는 목록)


    // 팔로잉 목록 조회 (내가 팔로우 하는 목록)


    // 팔로우 삭제 (unfollow)
}
