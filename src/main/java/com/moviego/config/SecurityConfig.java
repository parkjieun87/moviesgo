package com.moviego.config;

import com.moviego.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 💡 SecurityFilterChain 빈을 추가합니다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 🚀 회원가입, 로그인, Swagger, 오류 페이지 등 인증이 필요 없는 경로 허용
                        .requestMatchers(
                                // ★★★ 사용자 등록(POST) 및 로그인(일반적으로 GET/POST) API 허용
                                HttpMethod.POST, "/api/auth/*" // 사용자 등록 POST 허용
                        ).permitAll()

                        // ★★★ 기타 인증이 필요 없는 필수 경로 허용
                        .requestMatchers(
                                "/api/kofic/**",
                                "/api/movie/**",
                                "/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/api-docs/swagger-config",
                                "/login",
                                "/error" // 오류 처리 경로 허용
                        ).permitAll()

                        // 나머지 모든 요청은 인증되어야 합니다.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
