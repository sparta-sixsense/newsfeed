package com.sixsense.newsfeed.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// example
public record SignUpRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String name,
        @NotBlank String address,
        @NotNull Integer age
) {

}
