package com.pawland.chat.repository;

import com.pawland.chat.domain.ChatMessage;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessage> getChatMessageHistory(String roomId, String messageTime, int pageSize);
}
