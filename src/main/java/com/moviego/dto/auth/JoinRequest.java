package com.moviego.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 시 필요한 사용자 데이터")
public class JoinRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "사용자 이메일 (로그인 ID)", example = "newuser@moviego.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Schema(description = "사용자 비밀번호")
    private String password;
}
