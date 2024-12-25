package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.PasswordEncoder;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.*;
import com.sixsense.newsfeed.error.exception.*;
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
        User findUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // 활성 유저인지 확인 (여기서 검증하면 'getById()'를 호출하는 쪽에서는 헷갈려서 한 번 더 검증할 수 있을 듯
        validateIsActiveUser(findUser);
        return findUser;
    }

    public User getByEmail(String email) {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        // 활성 유저인지 확인
        validateIsActiveUser(findUser);
        return findUser;
    }

    // 저장하기 전에 휴면 혹은, 삭제된 계정이 있는지 검증한 번 해야 함.
    @Transactional
    public SignUpResponseDto save(SignUpRequestDto requestDto) {
        // 중복된 유저 있을 시
        userRepository.findByEmail(requestDto.email())
                .ifPresent(user -> {
                    // 삭제되거나, 휴먼 상태인 계정일 경우에도 새로 가입하는 방식으로는 계정 활성화가 안 되게끔.
                    throw new UserConflictException();
                });

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User savedUser = userRepository.save(requestDto.toEntity(encodedPassword));
        return SignUpResponseDto.fromEntity(savedUser);
    }

    // 로그인 처리
    public LoginResponseDto authenticate(LoginRequestDto requestDto) {
        User findUser = getByEmail(requestDto.email());

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

        // update
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        findUser.update(requestDto, encodedPassword);

        return UpdateUserResponseDto.fromEntity(findUser);
    }

    @Transactional
    public void delete(Long id, String accessToken) {
        // 삭제 권한 확인
        validateIsAccessible(id, accessToken);

        User findUser = getById(id);

        // Soft deletion
        findUser.deleteMe();
    }

    // 권한 확인
    private void validateIsAccessible(Long id, String accessToken) {
        Long userId = tokenProvider.getUserId(accessToken);
        if (!id.equals(userId)) {
            throw new UserAccessDeniedException();
        }
    }

    private void validateIsActiveUser(User user) {
        if (user.getStatus() != Status.ACTIVE) {
            throw new UserInactiveOrDeletedException();
        }
    }
}
