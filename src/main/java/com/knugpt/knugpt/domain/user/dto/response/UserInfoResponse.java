package com.knugpt.knugpt.domain.user.dto.response;

import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.domain.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
public record UserInfoResponse(
        @NotBlank(message = "이메일은 필수 항목입니다")
        @Schema(description = "사용자의 이메일 주소(이메일 형식에 맞아야 함)", example = "donator@example.com")
        String email,

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Schema(description = "사용자의 닉네임")
        String nickname,

        @Schema(description = "사용자의 전공")
        String major,

        @Schema(description = "사용자의 학점")
        String score,

        @Schema(description = "사용자의 자기 소개")
        String introduction,

        @NotBlank(message = "사용자 재학여부는 필수 항목입니다.")
        @Schema(description = "사용자의 재학여부")
        String status

) {
    public static UserInfoResponse fromEntity(User user) {
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .major(user.getMajor())
                .score(user.getScore())
                .introduction(user.getIntroduction())
                .status(user.getStatus().getValue())
                .build();
    }
}
