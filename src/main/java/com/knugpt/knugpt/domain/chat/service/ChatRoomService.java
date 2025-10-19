package com.knugpt.knugpt.domain.chat.service;

import com.knugpt.knugpt.domain.chat.dto.request.ChatRoomCreateRequest;
import com.knugpt.knugpt.domain.chat.dto.request.ChatRoomTitleUpdateRequest;
import com.knugpt.knugpt.domain.chat.dto.response.ChatRoomCreatedResponse;
import com.knugpt.knugpt.domain.chat.dto.response.ChatRoomListResponse;
import com.knugpt.knugpt.domain.chat.entity.ChatRoom;
import com.knugpt.knugpt.domain.chat.repository.ChatRoomRepository;
import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.domain.user.repository.UserRepository;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomCreatedResponse createChatRoom(Long userId, ChatRoomCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        ChatRoom chatRoom = chatRoomRepository.save(
                ChatRoom.builder()
                        .title(request.firstQuery())
                        .user(user)
                        .build()
        );

        return ChatRoomCreatedResponse.of(chatRoom);
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
