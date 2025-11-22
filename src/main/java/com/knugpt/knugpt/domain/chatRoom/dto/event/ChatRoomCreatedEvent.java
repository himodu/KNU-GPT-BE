package com.knugpt.knugpt.domain.chatRoom.dto.event;

import com.knugpt.knugpt.domain.chatRoom.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "채팅방 생성 이벤트")
public record ChatRoomCreatedEvent(
        @Schema(description = "채팅방 ID")
        Long chatRoomId,

        @Schema(description = "첫 번째 질문")
        String firstQuery
) {
    public static ChatRoomCreatedEvent of(ChatRoom chatRoom, String firstQuery) {
        return ChatRoomCreatedEvent.builder()
                .chatRoomId(chatRoom.getId())
                .firstQuery(firstQuery)
                .build();
    }
}
