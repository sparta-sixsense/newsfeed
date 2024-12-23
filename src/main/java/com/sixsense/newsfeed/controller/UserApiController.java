package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.dto.LoginRequestDto;
import com.sixsense.newsfeed.dto.LoginResponseDto;
import com.sixsense.newsfeed.dto.SignUpRequestDto;
import com.sixsense.newsfeed.dto.SignUpResponseDto;
import com.sixsense.newsfeed.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserApiController {

    private final UserService userService;

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

}
