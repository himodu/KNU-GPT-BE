package com.knugpt.knugpt.domain.chat.repository;

import com.knugpt.knugpt.domain.chat.mongo.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
    Page<Chat> findAllByChatRoomId(Long chatRoomId, Pageable pageable);

}
