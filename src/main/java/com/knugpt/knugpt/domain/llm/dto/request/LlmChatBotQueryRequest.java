package com.knugpt.knugpt.domain.llm.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.chat.type.ChatType;
import com.knugpt.knugpt.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@Schema(description = "LLM 서버에 질문 채팅 생성 요청")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LlmChatBotQueryRequest(
        String question,
        List<Map<String, String>> previousChatHistories,
        Map<String, Object> privateInfo
) {
    public static LlmChatBotQueryRequest of(User user, List<Chat> chats, String question) {
        return LlmChatBotQueryRequest.builder()
                .question(question)
                .previousChatHistories(
                            chats.stream()
                                .map(chat -> Map.of(
                                        "role", chat.getType() == ChatType.USER ? "user" : "chatbot",
                                        "content", extractContent(chat)
                                        )
                                )
                                .toList()
                )
                .privateInfo(
                        user != null ? Map.of(
                                "name", user.getNickname(),
                                "status", user.getStatus().getLanguage(),
                                "major", user.getMajor() != null ? user.getMajor() : "모름",
                                "score", user.getScore() != null ? user.getScore() : "모름",
                                "introduction", user.getIntroduction() != null ? user.getIntroduction() : "모름"
                        ) : null
                )
                .build();
    }

    public static LlmChatBotQueryRequest onlyQuestionOf(String question) {
        return LlmChatBotQueryRequest.builder()
                .question(question)
                .build();
    }

    /**
     * ChatData 안에서 실제 메시지만 추출하는 메서드
     */
    private static String extractContent(Chat chat) {
        if (chat.getData() instanceof Chat.QuestionChatData q) {
            return q.getQuestion();
        } else if (chat.getData() instanceof Chat.AnswerChatData a) {
            return a.getAnswer();
        }
        return ""; // 혹시 모르니 기본값
    }
}
