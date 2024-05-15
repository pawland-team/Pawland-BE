package com.pawland.global.config.security;

import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.exception.ChatRoomException;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.global.config.security.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
public class PawlandPermissionEvaluator implements PermissionEvaluator {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        // TODO: 권한 확인이 필요한 엔티티 케이스 추가
        switch (targetType) {
            case "CHATROOM":
                ChatRoom targetChatRoom = chatRoomRepository.findById(Long.valueOf(targetId.toString()))
                    .orElseThrow(ChatRoomException.ChatRoomNotFound::new);
                return targetChatRoom.getBuyerId().equals(userId)
                    || targetChatRoom.getSellerId().equals(userId);
            default:
                return false;
        }
    }
}
