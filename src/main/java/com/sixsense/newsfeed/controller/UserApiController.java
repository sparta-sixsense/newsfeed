package com.sixsense.newsfeed.controller;

import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.*;
import com.sixsense.newsfeed.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserApiController {

    private final UserService userService;

    @GetMapping("/api/users/{id}")
    public ResponseEntity<GetUserResponseDto> getUser(@PathVariable Long id) {
        // 원래 컨트롤러단에 Entity를 노출하는 건 바람직하지 않지만 중복되는 메서드를 만드는 건 조금 부담
        User findUser = userService.getById(id);
        return ResponseEntity.ok()
                .body(GetUserResponseDto.fromEntity(findUser));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> createUser(@Valid @RequestBody SignUpRequestDto requestDto) {

        SignUpResponseDto response = userService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {

        LoginResponseDto response = userService.authenticate(requestDto);
        return ResponseEntity.ok()
                .body(response);
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<UpdateUserResponseDto> updateUser(@PathVariable Long id,
                                                            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                                            @Valid @RequestBody UpdateUserRequestDto requestDto) {

        UpdateUserResponseDto response = userService.updateUserResponseDto(id, accessToken, requestDto);
        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {

        userService.delete(id, accessToken);
        return ResponseEntity.ok()
                .build();
    }
}
