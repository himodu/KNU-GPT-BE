package com.knugpt.knugpt.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.global.common.PageInfo;
import com.knugpt.knugpt.global.common.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "채팅방별 채팅 목록 정보")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatListResponse extends SelfValidating<ChatListResponse> {
    @NotNull
    @Schema(description = "페이지네이션 정보")
    private final PageInfo pageInfo;

    @NotNull
    @Schema(description = "채팅 목록")
    private final List<ChatItemResponse> chats;

    @Builder
    private ChatListResponse(PageInfo pageInfo, List<ChatItemResponse> chats) {
        this.pageInfo = pageInfo;
        this.chats = chats;
        validateSelf();
    }

    public static ChatListResponse of(Page<Chat> chatPage) {
        return ChatListResponse.builder()
                .pageInfo(PageInfo.of(chatPage))
                .chats(
                        chatPage.getContent().stream()
                                .map(ChatItemResponse::of)
                                .toList()
                )
                .build();
    }
}
