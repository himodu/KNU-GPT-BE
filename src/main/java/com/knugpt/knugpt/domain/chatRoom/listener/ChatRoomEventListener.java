package com.knugpt.knugpt.domain.chatRoom.listener;

import com.knugpt.knugpt.domain.chatRoom.dto.event.ChatRoomCreatedEvent;
import com.knugpt.knugpt.domain.chatRoom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatRoomEventListener {
        private final ChatRoomService chatRoomService;

        @Async
        @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
        public void handleChatRoomCreatedEvent(ChatRoomCreatedEvent event) {
            chatRoomService.saveChatRoomTitle(event);
        }
}
