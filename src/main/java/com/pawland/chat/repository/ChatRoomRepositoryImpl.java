package com.pawland.chat.repository;

import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pawland.chat.domain.QChatRoom.chatRoom;
import static com.pawland.product.domain.QProduct.product;
import static com.pawland.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatRoomInfoResponse> getMyChatRoomList(Long userId) {
        return jpaQueryFactory
            .select(Projections.constructor(ChatRoomInfoResponse.class,
                chatRoom.id.as("roomId"),
                chatRoom.orderId,
                Projections.constructor(ChatRoomInfoResponse.UserInfo.class,
                    user.id,
                    user.nickname,
                    user.profileImage),
                Projections.constructor(ChatRoomInfoResponse.ProductInfo.class,
                    product.id,
                    product.price,
                    product.name.as("productName"),
                    product.thumbnailImageUrl.as("imageThumbnail"),
                    product.status.as("saleState"),
                    product.purchaserId.as("purchaser"))
            ))
            .from(chatRoom)
            .join(user)
            .on(user.id.eq(
                new CaseBuilder()
                    .when(chatRoom.sellerId.eq(userId)).then(chatRoom.buyerId)
                    .otherwise(chatRoom.sellerId)
            ))
            .where(chatRoom.buyerId.eq(userId).or(chatRoom.sellerId.eq(userId)))
            .join(product).on(product.id.eq(chatRoom.productId))
            .fetch();
    }
}
