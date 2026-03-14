package com.knugpt.knugpt.domain.llm.dto.response;

public record LlmChatBotAnswerStreamChunkResponse(
        String type,
        String content,
        String message
) {
}
