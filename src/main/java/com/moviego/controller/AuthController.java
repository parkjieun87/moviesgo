package com.moviego.controller;

import com.moviego.dto.auth.JoinRequest;
import com.moviego.dto.auth.JoinResponse;
import com.moviego.dto.auth.RegisterRequest;
import com.moviego.dto.auth.RegisterResponse;
import com.moviego.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 및 사용자 관리", description = "로그인, 회원가입, JWT 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.registerUser(request); // 서비스 계층 호출
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(null, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JoinResponse> LoginUser(
            @Valid @RequestBody JoinRequest joinRequest,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ){
        try {
            // 토큰이 있고 유효하면 바로 예외 발생
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (authService.validateToken(token)) {  // jwtUtil.validateToken() 래핑된 메서드가 AuthService에 있다고 가정
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new JoinResponse(null, null, null, "이미 로그인된 상태입니다."));
                }
            }

            JoinResponse response = authService.join(joinRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JoinResponse(null,null,null, e.getMessage()));
        }
    }

}
