package com.knugpt.knugpt.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailValidateRequest(
        @NotBlank(message = "이메일은 필수 항목입니다")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "이메일 형식을 지켜주세요"
        )
        @Schema(description = "사용자의 이메일 주소(이메일 형식에 맞아야 함)", example = "donator@example.com")
        String email,

        @NotBlank(message = "인증코드는 필수 항목입니다")
        @Pattern(
                regexp = "^\\d{4}$",
                message = "인증코드는 숫자 4자리 입니다."
        )
        @Schema(description = "이메일 인증 코드", example = "1234")
        String code
) {
}
