package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.LoginRequestDto;
import com.sixsense.newsfeed.dto.LoginResponseDto;
import com.sixsense.newsfeed.dto.SignUpRequestDto;
import com.sixsense.newsfeed.dto.SignUpResponseDto;
import com.sixsense.newsfeed.dto.ProfileResponseDto;
import com.sixsense.newsfeed.dto.ProfileUpdateRequestDto;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserApiController {

    private final UserService userService;

    @Autowired
    private TokenProvider tokenProvider; // 필드 주입

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> createUser(@Valid @RequestBody SignUpRequestDto requestDto) {
        log.info("/signup");

        SignUpResponseDto response = userService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        log.info("/login");

        LoginResponseDto response = userService.authenticate(requestDto);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/get/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {



        // 토큰을 사용하여 프로필 반환
        ProfileResponseDto profile = userService.getProfile(accessToken);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/set/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody ProfileUpdateRequestDto dto,
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        System.out.println("Hello, World!");
        // 2. 프로필 업데이트 처리
        userService.updateProfile(accessToken, dto);
        System.out.println("Hello, World!");
        // 3. 응답 반환
        return ResponseEntity.ok().build();
    }
}
