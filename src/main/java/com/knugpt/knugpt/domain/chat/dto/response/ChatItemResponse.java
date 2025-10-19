package com.knugpt.knugpt.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Schema(description = "채팅방별 채팅 개별 정보")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatItemResponse extends SelfValidating<ChatItemResponse> {
    @NotNull
    @Schema(description = "채팅 타입")
    private final String type;

    @NotNull
    @Schema(description = "채팅 생성 일시")
    private final String createdAt;

    @NotNull
    @Schema(description = "채팅 내용")
    private final ChatDataResponse chat;

    @Builder
    public ChatItemResponse(String type, String createdAt, ChatDataResponse chat) {
        this.type = type;
        this.createdAt = createdAt;
        this.chat = chat;
        validateSelf();
    }

    public static ChatItemResponse of(Chat chat) {
        return ChatItemResponse.builder()
                .type(chat.getType().getValue())
                .createdAt(chat.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .chat(ChatDataResponse.of(chat.getData()))
                .build();
    }

    @Getter
    @Schema(description = "")
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ChatDataResponse extends SelfValidating<ChatDataResponse> {

        @Schema(description = "채팅 내용")
        @NotNull
        private final Chat.ChatData chat;

        @Builder
        private ChatDataResponse(Chat.ChatData chat) {
            this.chat = chat;
            validateSelf();
        }

        public static ChatDataResponse of(Chat.ChatData chatData) {
            return ChatDataResponse.builder()
                    .chat(chatData)
                    .build();
        }
    }

}
