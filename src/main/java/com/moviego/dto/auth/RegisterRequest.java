package com.moviego.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "회원가입 요청 시 필요한 사용자 데이터")
public class RegisterRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "사용자 이메일 (로그인 ID)", example = "newuser@moviego.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Schema(description = "비밀번호", example = "Password!1234")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "생년월일 (MM-DD 형식)", example = "01-01")
    @Pattern(regexp = "\\d{2}-\\d{2}", message = "생년월일은 MM-DD 형식이어야 합니다.") // 💡 유효성 검사 추가
    private String birthDate;
}
