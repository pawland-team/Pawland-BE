package com.pawland.chat.service;

import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ProductJpaRepository productJpaRepository;

    @Transactional
    public void createChatRoom(Long userId, ChatRoomCreateRequest request) {
        validateChatRoomCreateRequest(request);
        chatRoomRepository.save(request.toChatRoomWithMyId(userId));
    }

    public List<ChatRoomInfoResponse> getChatRoomList(Long userId) {
        return chatRoomRepository.getMyChatRoomList(userId);
    }

    private void validateChatRoomCreateRequest(ChatRoomCreateRequest request) {
        userRepository.findById(request.getSellerId())
            .orElseThrow(UserException.NotFoundUser::new);
        productJpaRepository.findById(request.getProductId())
            .orElseThrow(ProductException.NotFoundProduct::new);
    }
}
