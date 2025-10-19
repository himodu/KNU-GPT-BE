package com.knugpt.knugpt.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.domain.chat.dto.request.ChatRoomCreateRequest;
import com.knugpt.knugpt.domain.chat.entity.ChatRoom;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatRoomCreatedResponse extends SelfValidating<ChatRoomCreatedResponse> {
    @NotNull(message = "채탕방 ID는 비어있을 수 없습니다.")
    @Schema(description = "채팅방의 PK")
    Long chatRoomId;


    @Builder
    public ChatRoomCreatedResponse(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
        validateSelf();
    }

    public static ChatRoomCreatedResponse of(ChatRoom chatRoom) {
        return ChatRoomCreatedResponse.builder()
                .chatRoomId(chatRoom.getId())
                .build();
    }
}
