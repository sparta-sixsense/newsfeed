package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.*;
import com.sixsense.newsfeed.error.exception.AuthenticationException;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(
            summary = "프로필 정보 조회",
            description = "사용자의 AccessToken을 이용하여 프로필 정보를 반환합니다. 추가로 RefreshToken DTO를 포함하여 요청할 수 있습니다.",
            tags = {"Profile Management"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공적으로 조회된 프로필 정보 반환",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @GetMapping("/api/users/{id}")
    public ResponseEntity<ProfileResponseDto> getProfile(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        // 토큰을 사용하여 프로필 반환
        ProfileResponseDto profile = userService.getProfile(accessToken);
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "프로필 업데이트",
            description = "사용자의 AccessToken을 검증한 후 제공된 프로필 정보를 업데이트합니다.",
            tags = {"Profile Management"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공적으로 프로필이 업데이트됨",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @PutMapping("/api/users/{id}")
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid ProfileUpdateRequestDto dto,
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {

        userService.updateProfile(accessToken, dto);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "프로필 삭제",
            description = "사용자의 AccessToken과 삭제 요청 정보를 이용해 프로필을 삭제합니다.",
            tags = {"Profile Management"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공적으로 프로필이 삭제됨",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteProfile(@RequestBody ProfileDeleteRequestDto dto, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        userService.deleteProfile(accessToken, dto);
        return ResponseEntity.ok().build();
    }
}
