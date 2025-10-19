package com.knugpt.knugpt.domain.chat.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.knugpt.knugpt.domain.chat.type.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Document(collection = "chat")
@NoArgsConstructor
public class Chat {
    @Id
    private String id;

    private ChatType type;
    private ChatData data;
    private LocalDateTime createdAt;

    @Indexed
    private Long chatRoomId;

    @Builder
    public Chat(String id, ChatType type, ChatData data, LocalDateTime createdAt, Long chatRoomId) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
        this.chatRoomId = chatRoomId;
    }

    public static Chat questionOf(Long userId, Long chatRoomId, String question) {
        return Chat.builder()
                .type(ChatType.QUESTION)
                .data(
                        QuestionChatData.builder()
                                .userId(userId)
                                .question(question)
                                .build()
                )
                .createdAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .chatRoomId(chatRoomId)
                .build();
    }

    public static Chat answerOf(Long chatRoomId, String answer) {
        return Chat.builder()
                .type(ChatType.ANSWER)
                .data(
                        AnswerChatData.builder()
                                .answer(answer)
                                .build()
                )
                .createdAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .chatRoomId(chatRoomId)
                .build();
    }

    public interface ChatData {}

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionChatData implements ChatData {
        @JsonProperty("user_id")
        private Long userId;
        private String question;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerChatData implements ChatData {
        private String answer;
    }

}
