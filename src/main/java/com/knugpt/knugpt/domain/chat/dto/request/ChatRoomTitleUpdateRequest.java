package com.knugpt.knugpt.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatRoomTitleUpdateRequest(
        @NotBlank(message = "채팅창 제목은 바꿀 수 없습니다.")
        @Schema(description = "채팅방 새 제목")
        @Size(max = 20, message = "최대 20글자입니다.")
        String title
) {
}
