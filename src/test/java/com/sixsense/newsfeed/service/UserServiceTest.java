package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.constant.Token;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.LoginRequestDto;
import com.sixsense.newsfeed.dto.SignUpRequestDto;
import com.sixsense.newsfeed.dto.UpdateUserRequestDto;
import com.sixsense.newsfeed.dto.UpdateUserResponseDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.AuthenticationException;
import com.sixsense.newsfeed.error.exception.UserConflictException;
import com.sixsense.newsfeed.error.exception.UserInactiveOrDeletedException;
import com.sixsense.newsfeed.error.exception.UserNotFoundException;
import com.sixsense.newsfeed.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static com.sixsense.newsfeed.error.ErrorCode.AUTHENTICATION_FAILURE;
import static com.sixsense.newsfeed.error.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    String testEmail = "test@gmail.com";

    User testUser = User.builder()
            .name("하하하")
            .email(testEmail)
            .age(10)
            .address("뉴욕특파원시 맨해튼동")
            .password("123")
            .build();

    @BeforeEach
    void beforeEachTest() {
        userRepository.deleteAllInBatch();
    }


    @DisplayName("회원가입시 동일 이메일이 있으면 실패")
    @Test
    void signUpFailsTest() {
        // then
        userRepository.save(testUser);
        SignUpRequestDto requestDto = new SignUpRequestDto(testEmail, "123", "강성욱", "https://~~", "123", 10);

        // when & then
        assertThatThrownBy(() -> {
            userService.save(requestDto);
        })
                .isInstanceOf(UserConflictException.class)
                .hasMessageContaining(ErrorCode.USER_CONFLICT.getMessage());
    }

    @DisplayName("회원가입 성공")
    @Test
    void signUpSuccessTest() {
        // then
        SignUpRequestDto requestDto = new SignUpRequestDto(testEmail, "123", "강성욱",
                "https://~", "123", 10);
        userService.save(requestDto);

        // when
        User findUser = userService.getByEmail(testEmail);

        // then
        assertThat(findUser.getEmail()).isEqualTo(requestDto.email());
        assertThat(findUser.getName()).isEqualTo(requestDto.name());
        assertThat(findUser.getAddress()).isEqualTo(requestDto.address());
        assertThat(findUser.getAge()).isEqualTo(requestDto.age());
    }

    @DisplayName("가입된 이력이 없는 상태에서 로그인 시도")
    @Test
    void loginFailsTest() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto(testEmail, "123");

        // when & then
        assertThatThrownBy(() -> {
            userService.authenticate(requestDto);
        }).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND.getMessage());
    }

    @DisplayName("로그인 시, 비밀번호가 일치하지 않을 때")
    @Test
    void loginFailsWhenPasswordDoesNotMatch() {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(testEmail, "123",
                "강성욱", "https://~~", "123", 10);
        userService.save(signUpRequestDto);

        // then & when
        LoginRequestDto loginRequestDto = new LoginRequestDto(testEmail, "1234");
        assertThatThrownBy(() -> {
            userService.authenticate(loginRequestDto);
        }).isInstanceOf(AuthenticationException.class)
                .hasMessage(AUTHENTICATION_FAILURE.getMessage());
    }

    @DisplayName("회원 정보 업데이트")
    @Test
    void updateUserInfo() {
        // given
        User savedUser = userRepository.save(testUser);
        String accessToken = Token.BEARER_PREFIX + tokenProvider.generateToken(savedUser, Duration.ofDays(1));

        // when
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("1234",
                "김치치즈스마일", "https://~~", "사랑시 고백구 행복동", 100);
        UpdateUserResponseDto responseDto = userService.updateUserResponseDto(savedUser.getId(), accessToken, requestDto);

        // then
        User findUser = userService.getById(savedUser.getId());
        assertThat(findUser.getName()).isEqualTo(responseDto.name());
        assertThat(findUser.getAddress()).isEqualTo(responseDto.address());
        assertThat(findUser.getAge()).isEqualTo(responseDto.age());
    }

    @DisplayName("회원 삭제")
    @Test
    void deleteUser() {
        // given
        User savedUser = userRepository.save(testUser);
        String accessToken = tokenProvider.generateToken(savedUser, Duration.ofDays(1));

        // when
        userService.delete(savedUser.getId(), accessToken);

        // then
        User findUser = userRepository.findById(savedUser.getId()).get();
        assertThat(findUser.getStatus()).isEqualTo(Status.DELETED);
        assertThatThrownBy(() -> {
            userService.getById(savedUser.getId());
        })
                .isInstanceOf(UserInactiveOrDeletedException.class)
                .hasMessage(ErrorCode.USER_INACTIVE_OR_DELETED.getMessage());

    }

}