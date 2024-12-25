package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.CreateAccessTokenRequestDto;
import com.sixsense.newsfeed.dto.CreateAccessTokenResponseDto;
import com.sixsense.newsfeed.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;

@RestController
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    // 사실 쓸 일은 없다.
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponseDto> reissueNewAccessToken(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                                                             @Valid @RequestBody CreateAccessTokenRequestDto request) {

        String newAccessToken = tokenService.createNewAccessToken(accessToken, request.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponseDto(newAccessToken));
    }
}
