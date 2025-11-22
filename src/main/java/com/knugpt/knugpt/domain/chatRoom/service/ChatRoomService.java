package com.knugpt.knugpt.domain.chatRoom.service;

import com.knugpt.knugpt.domain.chatRoom.dto.event.ChatRoomCreatedEvent;
import com.knugpt.knugpt.domain.chatRoom.dto.request.ChatRoomCreateRequest;
import com.knugpt.knugpt.domain.chatRoom.dto.request.ChatRoomTitleUpdateRequest;
import com.knugpt.knugpt.domain.chatRoom.dto.response.ChatRoomCreatedResponse;
import com.knugpt.knugpt.domain.chatRoom.dto.response.ChatRoomListResponse;
import com.knugpt.knugpt.domain.chatRoom.entity.ChatRoom;
import com.knugpt.knugpt.domain.chatRoom.repository.ChatRoomRepository;
import com.knugpt.knugpt.domain.llm.LlmClient;
import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.domain.user.repository.UserRepository;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final LlmClient lLmClient;

    @Transactional
    public ChatRoomCreatedResponse createChatRoom(Long userId, ChatRoomCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        ChatRoom chatRoom = chatRoomRepository.save(
                ChatRoom.builder()
                        .title(request.firstQuery())
                        .user(user)
                        .build()
        );

        eventPublisher.publishEvent(ChatRoomCreatedEvent.of(chatRoom, request.firstQuery()));

        return ChatRoomCreatedResponse.of(chatRoom);
    }

    @Transactional
    public void saveChatRoomTitle(ChatRoomCreatedEvent event) {
        ChatRoom chatRoom  = chatRoomRepository.findById(event.chatRoomId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM));

        String title = lLmClient.summarizeQueryForTitle(event.firstQuery());

        if (title == null || title.isBlank()) {
            String query = event.firstQuery();

            title = query.length() > 15 ? query.substring(0, 15) : query;
        }

        chatRoom.updateTitle(title);
    }


    public ChatRoomListResponse getChatRooms(Long userId, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserId(userId, pageable);

        return ChatRoomListResponse.of(chatRoomList);
    }

    public void updateChatRoomTitle(Long userId, Long chatRoomId, ChatRoomTitleUpdateRequest request) {

        ChatRoom chatRoom = chatRoomRepository.findByIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM));

        chatRoom.updateTitle(request.title());
        chatRoomRepository.save(chatRoom);
    }

    public void deleteChatRoom(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM));

        chatRoomRepository.delete(chatRoom);
    }

}
