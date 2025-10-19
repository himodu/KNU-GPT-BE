package com.knugpt.knugpt.domain.chat.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatRoomCreateRequest(
        @NotBlank(message = "첫 질문은 비어있을 수 없습니다.")
        @Schema(description = "채팅방 첫 질문")
        String firstQuery
) {
}
