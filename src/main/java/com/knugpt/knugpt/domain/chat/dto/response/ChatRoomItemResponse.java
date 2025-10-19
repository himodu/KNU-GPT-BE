package com.knugpt.knugpt.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.domain.chat.entity.ChatRoom;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.aspectj.weaver.ast.Not;

@Getter
@Schema(description = "사용자별 채팅방 개별 정보")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatRoomItemResponse extends SelfValidating<ChatRoomItemResponse> {

    @NotNull
    @Schema(description = "채탕방 PK")
    private final Long chatRoomId;

    @NotNull
    @Schema(description = "채팅방 제목")
    private final String chatRoomTitle;

    @Builder
    private ChatRoomItemResponse(Long chatRoomId, String chatRoomTitle) {
        this.chatRoomId = chatRoomId;
        this.chatRoomTitle = chatRoomTitle;
        validateSelf();
    }

    public static ChatRoomItemResponse of(ChatRoom chatRoom) {
        return ChatRoomItemResponse.builder()
                .chatRoomId(chatRoom.getId())
                .chatRoomTitle(chatRoom.getTitle())
                .build();
    }
}
