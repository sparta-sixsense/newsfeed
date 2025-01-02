package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.FollowRelation;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.GetFollowingResponseDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.UserInactiveOrDeletedException;
import com.sixsense.newsfeed.repository.FollowRelationRepository;
import com.sixsense.newsfeed.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static com.sixsense.newsfeed.constant.Token.BEARER_PREFIX;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class FollowManagementServiceTest {

    @Autowired
    FollowManagementService followManagementService;

    @Autowired
    FollowRelationRepository followRelationRepository;

    @Autowired
    UserRepository userRepository;

    User user1, user2;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void beforeEachTest() {
        log.info("------ <@BeforeEach start> ------");
        /**
         * deleteAll() 메서드를 사용하면 모든 유저를 우선 가져온 다음에, 유저 하나씩 delete 쿼리를 쏜다.
         * 그렇기에 이미 모든 유저를 가져와서 메모리에 로드할 때부터 엄청난 오버헤드가 발생할뿐더러 삭제할 때도
         * delete 쿼리를 하나씩 쏘기 때문에 또한 엄청난 오버헤드가 발생한다.
         */
//        userRepository.deleteAll();

        followRelationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        user1 = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("사랑시 고백구 행복동")
                .age(20)
                .build();

        user2 = User.builder()
                .name("user2")
                .email("user2@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("사랑시 설렘구 불행동")
                .age(21)
                .build();

        userRepository.saveAll(List.of(user1, user2));
        log.info("------ <@BeforeEach end> ------");
    }

    @AfterEach
    void afterAll() {
        followRelationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("유저 두 개가 모두 팔로우 상태면 팔로우 신청 성공")
    @Test
    void followTest() {
        // given
        String requesterAccessToken = tokenProvider.generateToken(user1, Duration.ofHours(1));

        // when
        followManagementService.follow(requesterAccessToken, user1.getId(), user2.getId());
        FollowRelation findFollowRelation = followRelationRepository.findAll().get(0);

        // then
        assertThat(findFollowRelation.getRequester().getId()).isEqualTo(user1.getId());
        assertThat(findFollowRelation.getAccepter().getId()).isEqualTo(user2.getId());
    }

    @DisplayName("유저 중 하나라도 비활성 상태면 팔로우 불가")
    @Test
    @Transactional
    void followFailsTest() {
        // given
        String requesterAccessToken = tokenProvider.generateToken(user1, Duration.ofHours(1));
//        requester.deleteMe();
        user2.deleteMe();

        log.info("is requester persisting? {}", entityManager.contains(user1));
        log.info("is accepter persisting? {}", entityManager.contains(user2));

        // when & then
        assertThatThrownBy(() -> {
            followManagementService.follow(requesterAccessToken, user1.getId(), user2.getId());
        }).isInstanceOf(UserInactiveOrDeletedException.class)
                .hasMessage(ErrorCode.USER_INACTIVE_OR_DELETED.getMessage());
    }

    @DisplayName("현재 팔로잉 중인 데이터 모두 가져오기")
    @Transactional
    @Test
    void getFollowingsTest() {
        // given
        String requesterAccessToken = BEARER_PREFIX + tokenProvider.generateToken(user1, Duration.ofHours(1));

        User user3 = User.builder()
                .name("user3")
                .email("user3@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("이혼식 설렘구 오작교동")
                .age(21)
                .build();
        userRepository.save(user3);

        // when
        followManagementService.follow(requesterAccessToken, user1.getId(), user2.getId());
        followManagementService.follow(requesterAccessToken, user1.getId(), user3.getId());
        List<GetFollowingResponseDto> followings = followManagementService.getFollowings(user1.getId());

        // then
        assertThat(followings.size()).isEqualTo(2);
    }

    @DisplayName("비활성화 된 유저를 제외한 현재 팔로잉 중인 데이터 모두 가져오기")
    @Test
    @Transactional
    void getOnlyActiveFollowingsTest() {
        // given
        String requesterAccessToken = tokenProvider.generateToken(user1, Duration.ofHours(1));
        User user3 = User.builder()
                .name("user3")
                .email("user3@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("이혼식 설렘구 오작교동")
                .age(21)
                .build();
        userRepository.save(user3);

        // when
        followManagementService.follow(requesterAccessToken, user1.getId(), user2.getId());
        followManagementService.follow(requesterAccessToken, user1.getId(), user3.getId());
//        user3.deleteMe();
        user2.deleteMe();
        log.info("user2 상태 변경 후");

        List<GetFollowingResponseDto> followings = followManagementService.getFollowings(user1.getId());
        log.info("Followings 조회 완료");

        // then
        assertThat(followings.size()).isEqualTo(1);
    }

    @DisplayName("팔로잉 관계 삭제 테스트")
    @Test
    @Transactional
    void removeFollowingTest() {
        // given
        String requesterAccessToken = tokenProvider.generateToken(user1, Duration.ofHours(1));

        // when & then
        followManagementService.follow(requesterAccessToken, user1.getId(), user2.getId());
        List<GetFollowingResponseDto> followings = followManagementService.getFollowings(user1.getId());
        assertThat(followings.size()).isEqualTo(1);

        followManagementService.deleteFollowing(requesterAccessToken, user1.getId(), user2.getId());
        List<GetFollowingResponseDto> followings2 = followManagementService.getFollowings(user1.getId());
        assertThat(followings2.size()).isZero();
    }

    @Test
    void pagingTest() {
        // given
        String requesterAccessToken = tokenProvider.generateToken(user1, Duration.ofHours(1));

        // when
        followManagementService.follow(requesterAccessToken, user1.getId(), user2.getId());

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<FollowRelation> result = followRelationRepository.searchSimplePage(pageRequest);

        // then
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).extracting("id").containsExactly(1L);
        assertThat(result.getContent()).extracting("requester.id").containsExactly(1L);
        assertThat(result.getContent()).extracting("accepter.id").containsExactly(2L);
    }
}