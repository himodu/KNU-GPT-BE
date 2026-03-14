package com.knugpt.knugpt.domain.chat.repository;

import com.knugpt.knugpt.domain.chat.mongo.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatCustomRepositoryImpl implements ChatCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public void saveUserQuestion(Long userId, Long chatRoomId, String question) {
        mongoTemplate.save(Chat.userOf(userId, chatRoomId, question));
    }

    @Override
    public List<Chat> getRecentChats(Long chatRoomId, int size) {
        Query query = new Query()
                .addCriteria(Criteria.where("chatRoomId").is(chatRoomId))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .limit(size);

        return mongoTemplate.find(query, Chat.class);
    }

    @Override
    public void saveChatbotAnswer(Long chatRoomId, String answer) {
        mongoTemplate.save(Chat.chatbotOf(chatRoomId, answer));
    }
}
