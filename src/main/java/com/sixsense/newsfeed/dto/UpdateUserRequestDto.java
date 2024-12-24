package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequestDto(

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,14}$",
                message = "비밀번호는 대소문자 포함 영문, 숫자, 특수 문자 최소 1글자를 포함하며 최소 8글자 이상으로 이루어져야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Pattern(
                regexp = "^[a-zA-Z가-힣]{1,12}",
                message = "이름은 영문자, 한글만 사용 가능하며 2자에서 12자 사이여야 합니다."
        )
        String name,

        String address,
        Integer age
) {
}
