package com.pawland.chat.repository;

import com.pawland.chat.dto.response.ChatRoomInfoResponse;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoomInfoResponse> getMyChatRoomList(Long userId);
}
