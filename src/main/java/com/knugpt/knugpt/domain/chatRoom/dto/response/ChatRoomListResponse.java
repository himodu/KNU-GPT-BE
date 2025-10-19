package com.knugpt.knugpt.domain.chatRoom.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.domain.chatRoom.entity.ChatRoom;
import com.knugpt.knugpt.global.common.PageInfo;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "사용자별 채팅방 목록 정보")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatRoomListResponse extends SelfValidating<ChatRoomListResponse> {

    @NotNull
    @Schema(description = "페이지네이션 정보")
    private final PageInfo pageInfo;

    @NotNull
    @Schema(description = "채팅방 목록")
    private final List<ChatRoomItemResponse> chatRooms;

    @Builder
    private ChatRoomListResponse(PageInfo pageInfo, List<ChatRoomItemResponse> chatRooms) {
        this.pageInfo = pageInfo;
        this.chatRooms = chatRooms;
        validateSelf();
    }

    public static ChatRoomListResponse of(Page<ChatRoom> chatRoomPage) {
        return ChatRoomListResponse.builder()
                .pageInfo(PageInfo.of(chatRoomPage))
                .chatRooms(
                        chatRoomPage.getContent().stream()
                                .map(ChatRoomItemResponse::of)
                                .toList()
                )
                .build();

    }

}
