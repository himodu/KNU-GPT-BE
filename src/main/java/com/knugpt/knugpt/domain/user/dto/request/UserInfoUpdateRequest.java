package com.knugpt.knugpt.domain.user.dto.request;

import com.knugpt.knugpt.domain.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserInfoUpdateRequest(

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Schema(description = "사용자의 닉네임")
        String nickname,

        @Schema(description = "사용자의 전공")
        String major,

        @Schema(description = "사용자의 학점")
        String score,

        @Schema(description = "사용자의 자기 소개")
        String introduction,

        @NotNull(message = "사용자 재학여부는 필수 항목입니다.")
        @Schema(description = "사용자의 재학여부")
        UserStatus status

) {
}
