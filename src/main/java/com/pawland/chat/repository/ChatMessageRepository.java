package com.pawland.chat.repository;

import com.pawland.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {

    @Query(value = "SELECT * FROM chat_message cm WHERE cm.room_id = :roomId ORDER BY message_time DESC LIMIT 1", nativeQuery = true)
    Optional<ChatMessage> getLastMessage(@Param("roomId") Long roomId);
}
