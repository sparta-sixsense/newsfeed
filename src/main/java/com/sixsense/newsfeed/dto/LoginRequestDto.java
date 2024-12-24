package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "이메일은 필수값입니다.") @Email(message = "이메일 형식에 어긋납니다.") String email,
        @NotBlank(message = "비밀번호를 입력해주세요.") String password
) {
}
