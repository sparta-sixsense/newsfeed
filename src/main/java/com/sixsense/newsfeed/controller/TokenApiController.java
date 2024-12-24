package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.CreateAccessTokenRequestDto;
import com.sixsense.newsfeed.dto.CreateAccessTokenResponseDto;
import com.sixsense.newsfeed.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;

@Tag(name = "Token Management", description = "토큰 재발급 관련 API")
@RestController
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    // 사실 쓸 일은 없다.
    @Operation(
            summary = "토큰 재발급",
            description = "토큰이 만료되었을 때, accessToken과 refreshToken의 userId값을 비교해 accessToken을 재발급합니다.",
            tags = {"Token Management"}
    )
    @ApiResponse(
            responseCode = "201",
            description = "성공적으로 재발급된 accessToken 반환",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponseDto> reissueNewAccessToken(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                                                              @Valid
                                                                              @RequestBody
                                                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                                      description = "기존에 발급받은 Refresh 토큰 DTO",
                                                                                      required = true,
                                                                                      content = @Content(
                                                                                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                                              schema = @Schema(implementation = CreateAccessTokenRequestDto.class)
                                                                                      )
                                                                              )
                                                                              CreateAccessTokenRequestDto request) {

        String newAccessToken = tokenService.createNewAccessToken(accessToken, request.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponseDto(newAccessToken));
    }
}
