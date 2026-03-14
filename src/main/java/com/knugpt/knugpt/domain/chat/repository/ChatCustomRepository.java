package com.knugpt.knugpt.domain.chat.repository;

import com.knugpt.knugpt.domain.chat.mongo.Chat;

import java.util.List;

public interface ChatCustomRepository {
    void saveUserQuestion(Long userId, Long chatRoomId, String question);
    List<Chat> getRecentChats(Long chatRoomId, int size);
    void saveChatbotAnswer(Long chatRoomId, String answer);
}
