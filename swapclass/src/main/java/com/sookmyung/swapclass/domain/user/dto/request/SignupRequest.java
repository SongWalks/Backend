package com.sookmyung.swapclass.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank @Email String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,12}$",
                message = "비밀번호는 8~12자, 영문·숫자·특수문자를 모두 포함해야 합니다."
        )
        String password,

        @NotBlank String passwordConfirm
) {}
