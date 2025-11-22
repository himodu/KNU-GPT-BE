package com.knugpt.knugpt.domain.llm.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LlmChatBotAnswerResponse extends SelfValidating<LlmChatBotAnswerResponse> {
    @NotNull
    @Schema(description = "챗봇 응답")
    private final String answer;

    @Builder
    public LlmChatBotAnswerResponse(String answer) {
        this.answer = answer;
    }
}
