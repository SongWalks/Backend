package com.sookmyung.swapclass.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank String refreshToken
) {}
