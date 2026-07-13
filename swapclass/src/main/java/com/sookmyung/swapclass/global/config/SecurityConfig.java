package com.sookmyung.swapclass.global.config;

import com.sookmyung.swapclass.global.jwt.JwtAuthenticationFilter;
import com.sookmyung.swapclass.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // 필터에 넘겨줄 토큰 검증 도구

    // 비밀번호 해싱기(BCrypt). AuthService가 주입받아 encode/matches에 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT 기반이라 CSRF 불필요 → 끔
                .csrf(csrf -> csrf.disable())
                // 세션을 안 쓰는 stateless 방식(토큰으로만 인증)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 경로별 접근 권한
                .authorizeHttpRequests(auth -> auth
                        // 토큰 없이 접근 가능한 공개 경로(회원가입/로그인/인증/재발급/테스트)
                        .requestMatchers(
                                "/api/auth/email/code",
                                "/api/auth/email/verify",
                                "/api/auth/email/exists",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/token/refresh",
                                "/api/test/**",
                                // Swagger UI / API 문서
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // 그 외(로그아웃 포함)는 인증 필요 (중복 부분 제거 완료)
                        .anyRequest().authenticated()
                )
                // 기본 로그인 방식 끔(우리는 JWT 사용)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                // 스프링 기본 인증 필터 앞에 우리 JWT 필터를 끼워 넣음
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}