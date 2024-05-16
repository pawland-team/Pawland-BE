package com.pawland.chat.service;

import com.pawland.chat.domain.ChatMessage;
import com.pawland.chat.dto.request.ChatMessageRequest;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.dto.response.ChatMessageHistoryResponse;
import com.pawland.chat.dto.response.ChatMessageResponse;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.chat.repository.ChatMessageRepository;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.order.exception.OrderException;
import com.pawland.order.respository.OrderJpaRepository;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private static final int CHAT_MESSAGE_HISTORY_SIZE = 10;

    @Transactional
    public void createChatRoom(Long userId, ChatRoomCreateRequest request) {
        validateChatRoomCreateRequest(request);
        chatRoomRepository.save(request.toChatRoomWithMyId(userId));
    }

    public List<ChatRoomInfoResponse> getChatRoomList(Long userId) {
        return chatRoomRepository.getMyChatRoomList(userId);
    }

    @Transactional
    public ChatMessageResponse saveMessage(String roomId, ChatMessageRequest request) {
        ChatMessage chatMessage = request.toChatMessageWith(Long.parseLong(roomId), LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.of(chatMessage);
    }

    public ChatMessageHistoryResponse getChatMessageHistory(String roomId, String messageTime) {
        List<ChatMessage> messageList = chatMessageRepository.getChatMessageHistory(roomId, messageTime, CHAT_MESSAGE_HISTORY_SIZE + 1);
        boolean hasNext = messageList.size() > CHAT_MESSAGE_HISTORY_SIZE;
        if (hasNext) {
            ChatMessage nextCursor = messageList.remove(CHAT_MESSAGE_HISTORY_SIZE);
            return ChatMessageHistoryResponse.of(nextCursor.getMessageTime().toString(), messageList);
        } else {
            return ChatMessageHistoryResponse.of(null, messageList);
        }
    }

    private void validateChatRoomCreateRequest(ChatRoomCreateRequest request) {
        userRepository.findById(request.getSellerId())
            .orElseThrow(UserException.NotFoundUser::new);
        productJpaRepository.findById(request.getProductId())
            .orElseThrow(ProductException.NotFoundProduct::new);
        orderJpaRepository.findById(request.getOrderId())
            .orElseThrow(OrderException.NotFoundOrder::new);
    }
}
