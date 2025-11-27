package com.moviego.filter;

import com.moviego.entity.Users;
import com.moviego.repository.UserRepository;
import com.moviego.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Authorization Header에서 JWT 추출
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // "Bearer " 제거

        // 2. JWT 유효성 검사
        if (!jwtUtil.validateToken(token)) {
            logger.warn("JWT 토큰이 유효하지 않습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 토큰에서 사용자 정보 추출
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. DB에서 사용자 조회
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Spring Security 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,          // Principal
                        null,          // Credentials
                        List.of(new SimpleGrantedAuthority("ROLE_USER")) // User 엔티티가 roles 가지고 있다 가정
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 6. SecurityContext에 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

}
