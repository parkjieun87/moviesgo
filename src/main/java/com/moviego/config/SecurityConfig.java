package com.moviego.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 💡 SecurityFilterChain 빈을 추가합니다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 🚀 Swagger 및 API 문서 경로에 대한 접근을 무조건 허용 (permitAll)
                        .requestMatchers(
                                "/api-docs",
                                "/v3/api-docs/**",

                                // 💡 Swagger UI 정적 리소스 경로
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/api-docs/swagger-config",
                                "/login"
                        ).permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
