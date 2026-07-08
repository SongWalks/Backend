package com.sookmyung.swapclass.domain.user.controller;

import com.sookmyung.swapclass.domain.user.dto.request.EmailCodeRequest;
import com.sookmyung.swapclass.domain.user.dto.request.EmailVerifyRequest;
import com.sookmyung.swapclass.domain.user.dto.request.LoginRequest;
import com.sookmyung.swapclass.domain.user.dto.request.SignupRequest;
import com.sookmyung.swapclass.domain.user.dto.request.TokenRefreshRequest;
import com.sookmyung.swapclass.domain.user.dto.response.EmailExistsResponse;
import com.sookmyung.swapclass.domain.user.dto.response.TokenResponse;
import com.sookmyung.swapclass.domain.user.service.AuthService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController                       // 이 클래스는 REST API 컨트롤러. 반환값이 그대로 응답 body가 됨
@RequiredArgsConstructor             // final 필드(AuthService)를 생성자 주입
@RequestMapping("/api/auth")         // 이 컨트롤러의 공통 경로 접두사
public class AuthController {

    private final AuthService authService; // 실제 로직은 서비스에 위임(컨트롤러는 연결만)

    // [1] 이메일 인증코드 발송
    @PostMapping("/email/code")
    public ApiResponse<Void> sendEmailCode(@Valid @RequestBody EmailCodeRequest request) {
        // @Valid: DTO의 검증(@NotBlank, @Email 등)을 실행. 실패하면 GlobalExceptionHandler가 처리
        authService.sendEmailCode(request.email());
        return ApiResponse.success(null, "인증코드를 발송했습니다.");
    }

    // [2] 이메일 인증코드 확인
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request) {
        authService.verifyEmailCode(request.email(), request.code());
        return ApiResponse.success(null, "이메일 인증이 완료되었습니다.");
    }

    // [3] 이메일 중복 확인 (쿼리 파라미터 ?email=...)
    @GetMapping("/email/exists")
    public ApiResponse<EmailExistsResponse> checkEmailExists(@RequestParam String email) {
        return ApiResponse.success(authService.checkEmailExists(email));
    }

    // [4] 회원가입 (성공 시 201 Created)
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ApiResponse.success(null, "회원가입이 완료되었습니다.");
    }

    // [5] 로그인 → 토큰 2개 반환
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    // [6] 로그아웃 → JWT 필터가 세팅한 인증정보(userId)를 꺼내 사용
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.success(null, "로그아웃되었습니다.");
    }

    // [7] 토큰 재발급 → 새 access/refresh 반환
    @PostMapping("/token/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }
}
