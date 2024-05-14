package com.pawland.chat.repository;

import com.pawland.chat.domain.ChatMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.pawland.chat.domain.QChatMessage.chatMessage;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatMessage> getChatMessageHistory(String roomId, String messageTime, int pageSize) {
        return jpaQueryFactory
            .selectFrom(chatMessage)
            .where(
                roomIdEq(roomId),
                messageTimeLt(messageTime)
            )
            .orderBy(chatMessage.messageTime.desc())
            .limit(pageSize)
            .fetch();
    }

    private BooleanExpression roomIdEq(String roomId) {
        Long roomIdToLong = Long.parseLong(roomId);
        return hasText(roomId) ? chatMessage.roomId.eq(roomIdToLong) : null;
    }

    private BooleanExpression messageTimeLt(String messageTime) {
        if (messageTime == null) {
            return null;
        }
        LocalDateTime cursorId = LocalDateTime.parse(messageTime);
        return chatMessage.messageTime.lt(cursorId);
    }
}
