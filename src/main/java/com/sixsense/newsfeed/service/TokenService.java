package com.sixsense.newsfeed.service;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.error.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sixsense.newsfeed.constant.Token.ACCESS_TOKEN_DURATION;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    public String createNewAccessToken(String expiredAccessToken, String refreshToken) {
        // 토큰 유효성 검사
        if (tokenProvider.isValidToken(expiredAccessToken) == false
                || tokenProvider.isValidToken(refreshToken) == false) {
            throw new InvalidTokenException();
        }

        Long userIdByAccessToken = tokenProvider.getUserId(expiredAccessToken);
        Long userIdByRefreshToken = tokenProvider.getUserId(refreshToken);

        // userId 동일한지 검증
        if (userIdByAccessToken != userIdByRefreshToken) {
            throw new InvalidTokenException();
        }

        User findUser = userService.getById(userIdByRefreshToken);
        return tokenProvider.generateToken(findUser, ACCESS_TOKEN_DURATION);
    }
}
