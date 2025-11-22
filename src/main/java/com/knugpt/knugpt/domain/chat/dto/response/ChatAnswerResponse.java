package com.knugpt.knugpt.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "질문 응답 채팅 정보")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatAnswerResponse extends SelfValidating<ChatAnswerResponse> {
    @NotNull
    @Schema(description = "질문 응답")
    private final String answer;

    @Builder
    public ChatAnswerResponse(String answer) {
        this.answer = answer;
    }

    public static ChatAnswerResponse of(String answer) {
        return ChatAnswerResponse.builder()
                .answer(answer)
                .build();
    }
}
