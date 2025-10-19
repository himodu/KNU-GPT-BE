package com.knugpt.knugpt.domain.chat.service;

import com.knugpt.knugpt.domain.LLM.LLmClient;
import com.knugpt.knugpt.domain.chat.dto.request.ChatQueryRequest;
import com.knugpt.knugpt.domain.chat.dto.response.AnswerChatResponse;
import com.knugpt.knugpt.domain.chat.dto.response.ChatListResponse;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.chat.repository.ChatRepository;
import com.knugpt.knugpt.domain.chatRoom.entity.ChatRoom;
import com.knugpt.knugpt.domain.chatRoom.repository.ChatRoomRepository;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final LLmClient lLmClient;

    public AnswerChatResponse queryToChatBot(Long userId, Long chatRoomId, ChatQueryRequest request) {
        // TODO - 채팅 로직 구현 : 일단은 한 번에 다 보내는 식으로 구현하고 추후 SSE 적용해서 리팩토링하자
        if(!chatRoomRepository.existsByIdAndUserId(chatRoomId, userId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        }

        Chat question = Chat.questionOf(userId, chatRoomId, request.question());
        chatRepository.save(question);

        Chat answer = Chat.answerOf(chatRoomId, "아직 준비 중입니다!");
        chatRepository.save(answer);

        return AnswerChatResponse.of("아직 준비 중입니다!");
    }

    public ChatListResponse getChats(Long userId, Long chatRoomId, Integer page, Integer size) {
        if(!chatRoomRepository.existsByIdAndUserId(chatRoomId, userId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Chat> chatPage = chatRepository.findAllByChatRoomId(chatRoomId, pageable);

        return ChatListResponse.of(chatPage);
    }


}
