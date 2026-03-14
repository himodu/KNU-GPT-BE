package com.knugpt.knugpt.domain.chat.dto.response;

public record ChatStreamResponse(
        String type,   // chunk, end, error
        String content
) {
    public static ChatStreamResponse start() {
        return new ChatStreamResponse("start", "");
    }

    public static ChatStreamResponse chunk(String content) {
        return new ChatStreamResponse("chunk", content);
    }

    public static ChatStreamResponse end() {
        return new ChatStreamResponse("end", "");
    }

    public static ChatStreamResponse error(String message) {
        return new ChatStreamResponse("error", message);
    }
}
