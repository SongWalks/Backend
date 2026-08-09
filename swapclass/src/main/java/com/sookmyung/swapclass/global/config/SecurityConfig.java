package com.sookmyung.swapclass.global.config;

import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.jwt.JwtAuthenticationFilter;
import com.sookmyung.swapclass.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // 필터에 넘겨줄 토큰 검증 도구
    private final UserRepository userRepository; // 정지 유저 체크용

    // 비밀번호 해싱기(BCrypt). AuthService가 주입받아 encode/matches에 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화 (아래 corsConfigurationSource Bean 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // JWT 기반이라 CSRF 불필요 → 끔
                .csrf(csrf -> csrf.disable())
                // 세션을 안 쓰는 stateless 방식(토큰으로만 인증)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 경로별 접근 권한
                .authorizeHttpRequests(auth -> auth
                        // 유저별 게시글 조회는 인증 필요 (아래 공개 GET 규칙보다 먼저 선언해야 /api/posts/* 에 안 걸림)
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts/me",
                                "/api/posts/my-seekers",
                                "/api/posts/my-targets"
                        ).authenticated()
                        // 게시글 목록/상세는 비로그인 조회 허용 (GET만)
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts",
                                "/api/posts/*"
                        ).permitAll()
                        // 토큰 없이 접근 가능한 공개 경로(회원가입/로그인/인증/재발급/테스트)
                        .requestMatchers(
                                "/api/auth/email/code",
                                "/api/auth/email/verify",
                                "/api/auth/email/exists",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/token/refresh",
                                // 비밀번호 재설정(로그인 전 사용자도 호출 가능해야 함)
                                "/api/auth/password/email/code",
                                "/api/auth/password/reset",
                                // 홈화면 통합 조회(비로그인 둘러보기 허용, 인증 optional)
                                "/api/home",
                                "/api/test/**",
                                // Swagger UI / API 문서
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                //웹소켓
                                "/ws/**"
                        ).permitAll()
                        // 그 외(로그아웃 포함)는 인증 필요 (중복 부분 제거 완료)
                        .anyRequest().authenticated()
                )
                // 기본 로그인 방식 끔(우리는 JWT 사용)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                // 스프링 기본 인증 필터 앞에 우리 JWT 필터를 끼워 넣음
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // CORS 정책: 프론트 개발서버(Vite) 및 배포 도메인 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 프론트 origin. credentials 사용 시 "*" 불가 → 명시적으로 나열
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://localhost:5173",
                "http://localhost:8080",
                "https://soo-frontend-git-develop-song-walks.vercel.app",
                "https://swapclass.duckdns.org"
                "https://soo-frontend-brown.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // 프론트가 응답에서 읽어야 하는 헤더(예: 토큰) 노출
        config.setExposedHeaders(List.of("Authorization"));
        // 쿠키/Authorization 헤더 전송 허용
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 프리플라이트 캐시(초)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}