package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.PasswordEncoder;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.LoginRequestDto;
import com.sixsense.newsfeed.dto.LoginResponseDto;
import com.sixsense.newsfeed.dto.SignUpRequestDto;
import com.sixsense.newsfeed.dto.SignUpResponseDto;
import com.sixsense.newsfeed.dto.ProfileResponseDto;
import com.sixsense.newsfeed.dto.ProfileUpdateRequestDto;
import com.sixsense.newsfeed.error.exception.AuthenticationException;
import com.sixsense.newsfeed.error.exception.UserConflictException;
import com.sixsense.newsfeed.error.exception.UserNotFoundException;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sixsense.newsfeed.constant.Token.ACCESS_TOKEN_DURATION;
import static com.sixsense.newsfeed.constant.Token.REFRESH_TOKEN_DURATION;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());
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
        if (!passwordEncoder.matches(requestDto.password(), findUser.getPassword())) { // 비밀번호 일치 여부 확인
            throw new AuthenticationException();
        }

        // AccessToken, RefreshToken 발급
        String accessToken = tokenProvider.generateToken(findUser, ACCESS_TOKEN_DURATION);
        String refreshToken = tokenProvider.generateToken(findUser, REFRESH_TOKEN_DURATION);
        return new LoginResponseDto(findUser.getId(), accessToken, refreshToken);
    }



    public ProfileResponseDto getProfile(String token) {
        // 1. 토큰에서 사용자 ID 추출
        Long userId = tokenProvider.getUserId(token);

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 3. ProfileResponseDto 생성
        return ProfileResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .address(user.getAddress())
                .build();
    }


    public void updateProfile(String token, ProfileUpdateRequestDto dto) {
        // 1. 토큰에서 사용자 ID 추출
        Long userId = tokenProvider.getUserId(token);

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException(ErrorCode.AUTHENTICATION_FAILURE);
        }

        // 4. 프로필 정보 업데이트
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getAge() != null) user.setAge(dto.getAge());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());

        // 5. 변경 사항 저장
        userRepository.save(user);
    }


}
