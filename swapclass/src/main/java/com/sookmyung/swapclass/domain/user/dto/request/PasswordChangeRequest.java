package com.sookmyung.swapclass.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 비밀번호 변경 요청 (회원가입과 동일한 비밀번호 정책 재사용)
public record PasswordChangeRequest(
        @NotBlank String currentPassword,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,12}$",
                message = "비밀번호는 8~12자, 영문·숫자·특수문자를 모두 포함해야 합니다."
        )
        String newPassword,

        @NotBlank String newPasswordConfirm
) {}
