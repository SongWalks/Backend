package com.sookmyung.swapclass.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 비밀번호 재설정 요청 (이메일 인증 완료 후, 회원가입과 동일한 비밀번호 정책 재사용)
public record PasswordResetRequest(
        @NotBlank @Email String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,12}$",
                message = "비밀번호는 8~12자, 영문·숫자·특수문자를 모두 포함해야 합니다."
        )
        String newPassword,

        @NotBlank String newPasswordConfirm
) {}
