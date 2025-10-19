package com.knugpt.knugpt.domain.chat.repository;

import com.knugpt.knugpt.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Page<ChatRoom> findAllByUserId(Long userId, Pageable pageable);

    Optional<ChatRoom> findByIdAndUserId(Long chatRoomId, Long userId);
}
