package com.knugpt.knugpt.domain.chat.service;

import com.knugpt.knugpt.domain.llm.LLmClient;
import com.knugpt.knugpt.domain.chat.dto.request.ChatQueryRequest;
import com.knugpt.knugpt.domain.chat.dto.response.AnswerChatResponse;
import com.knugpt.knugpt.domain.chat.dto.response.ChatListResponse;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.chat.repository.ChatRepository;
import com.knugpt.knugpt.domain.chatRoom.entity.ChatRoom;
import com.knugpt.knugpt.domain.chatRoom.repository.ChatRoomRepository;
import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
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

    @Transactional
    public AnswerChatResponse queryToChatBot(Long userId, Long chatRoomId, ChatQueryRequest request) {
        // TODO - 채팅 로직 구현 : 일단은 한 번에 다 보내는 식으로 구현하고 추후 SSE 적용해서 리팩토링하자

        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findByIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM));

        // 2. 질문 기록 채팅에 저장
        chatRepository.save(
                Chat.userOf(
                        userId,
                        chatRoomId,
                        request.question()
                )
        );

        // 3. 사용자 정보 및 이전 채팅 기록 불러오기
        User user = chatRoom.getUser();
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Chat> chatPage = chatRepository.findAllByChatRoomId(chatRoomId, pageable);

        // 4. LLM 챗봇 응답 수신 및 채팅에 저장
        String answer = lLmClient.queryToChatBotWithUserInfo(
                user,
                chatPage.getContent(),
                request.question()
        );
        chatRepository.save(
                Chat.chatbotOf(
                        chatRoomId,
                        answer
                )
        );

        // 5. 결과 반환
        return AnswerChatResponse.of(answer);
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
