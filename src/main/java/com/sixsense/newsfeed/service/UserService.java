package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.PasswordEncoder;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.*;
import com.sixsense.newsfeed.error.exception.AuthenticationException;
import com.sixsense.newsfeed.error.exception.UserAccessDeniedException;
import com.sixsense.newsfeed.error.exception.UserConflictException;
import com.sixsense.newsfeed.error.exception.UserNotFoundException;
import com.sixsense.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sixsense.newsfeed.constant.Token.ACCESS_TOKEN_DURATION;
import static com.sixsense.newsfeed.constant.Token.REFRESH_TOKEN_DURATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public SignUpResponseDto save(SignUpRequestDto requestDto) {

        // 중복된 유저 있을 시
        userRepository.findByEmail(requestDto.email())
                .ifPresent(user -> {
                    throw new UserConflictException();
                });

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User savedUser = userRepository.save(requestDto.toEntity(encodedPassword));
        return SignUpResponseDto.fromEntity(savedUser);
    }

    // 로그인 처리
    public LoginResponseDto authenticate(LoginRequestDto requestDto) {
        User findUser = getByEmail(requestDto.email());

        // 활성 유저인지 확인
        validateIsActiveUser(findUser);

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.password(), findUser.getPassword())) {
            throw new AuthenticationException();
        }

        // AccessToken, RefreshToken 발급
        String accessToken = tokenProvider.generateToken(findUser, ACCESS_TOKEN_DURATION);
        String refreshToken = tokenProvider.generateToken(findUser, REFRESH_TOKEN_DURATION);
        return new LoginResponseDto(findUser.getId(), accessToken, refreshToken);
    }

    @Transactional
    public UpdateUserResponseDto updateUserResponseDto(Long id, String accessToken, UpdateUserRequestDto requestDto) {
        // 업데이트 권한 확인
        validateIsAccessible(id, accessToken);

        User findUser = getById(id);

        // 활성 유저인지 확인
        validateIsActiveUser(findUser);

        String encodedPassword = passwordEncoder.encode(requestDto.password());
        findUser.update(requestDto, encodedPassword);

        return UpdateUserResponseDto.fromEntity(findUser);
    }

    @Transactional
    public void delete(Long id, String accessToken) {
        // 삭제 권한 확인
        validateIsAccessible(id, accessToken);

        User findUser = getById(id);

        // 활성 유저인지 확인
        validateIsActiveUser(findUser);

        // delete
        findUser.deleteMe();
    }

    // 권한 확인
    private void validateIsAccessible(Long id, String accessToken) {
        Long userId = tokenProvider.getUserId(accessToken);
        if (id != userId) {
            throw new UserAccessDeniedException();
        }
    }

    private void validateIsActiveUser(User user) {
        if (!user.getStatus().isActive()) {
            // 우선은 NotFound로 가자. 사실 ACTIVE, INACTIVE, DELETE <- 이렇게 3개 상태가 있어야 깔끔할 거 같은데
            throw new UserNotFoundException();
        }
    }
}
