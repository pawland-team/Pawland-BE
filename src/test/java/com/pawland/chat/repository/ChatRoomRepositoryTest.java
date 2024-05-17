package com.pawland.chat.repository;

import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.global.config.QueryDslConfig;
import com.pawland.order.respository.OrderJpaRepository;
import com.pawland.product.domain.Product;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.pawland.product.domain.Status.SELLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("local")
class ChatRoomRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @DisplayName("내 채팅방 목록 조회 시")
    @Nested
    class getMyChatRoomList{
        @DisplayName("내가 구매자인 채팅방과 판매자인 채팅방을 모두 조회한다.")
        @Test
        void getMyChatRoomList1() {
            // given
            User myAccount = createUser("본인", "midcon1@naver.com", "asd123123");
            User seller1 = createUser("판매자1", "midcon2@naver.com", "asd123123");
            User seller2 = createUser("판매자2", "midcon3@naver.com", "asd123123");
            User buyer1 = createUser("구매자1", "midcon4@naver.com", "asd123123");
            User buyer2 = createUser("구매자2", "midcon5@naver.com", "asd123123");
            userRepository.saveAll(List.of(myAccount, seller1, seller2, buyer1, buyer2));

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "강아지", "새상품");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "강아지", "새상품");
            Product product3 = createProduct("나는짱물건3", 3000, "장난감", "강아지", "새상품");
            Product product4 = createProduct("나는짱물건4", 4000, "장난감", "강아지", "새상품");
            product3.confirmPurchase(1L);
            productJpaRepository.saveAll(List.of(product1, product2, product3, product4));

            Long orderId1 = 1L;
            Long orderId2 = 2L;
            Long orderId3 = 3L;
            Long orderId4 = 4L;
            Long orderId5 = 5L;

            ChatRoom myChatRoom1 = createChatRoom(myAccount.getId(), seller1.getId(), orderId1, product1.getId());
            ChatRoom myChatRoom2 = createChatRoom(myAccount.getId(), seller2.getId(), orderId2, product2.getId());
            ChatRoom myChatRoom3 = createChatRoom(buyer1.getId(), myAccount.getId(), orderId3, product3.getId());
            ChatRoom notMyChatRoom1 = createChatRoom(buyer1.getId(), seller2.getId(), orderId4, product4.getId());
            ChatRoom notMyChatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), orderId5, product4.getId());
            chatRoomRepository.saveAll(List.of(myChatRoom1, myChatRoom2, myChatRoom3, notMyChatRoom1, notMyChatRoom2));

            // when
            List<ChatRoomInfoResponse> result = chatRoomRepository.getMyChatRoomList(myAccount.getId());

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting("orderId")
                .containsExactlyInAnyOrder(1L, 2L, 3L);
            assertThat(result).extracting("opponentUser")
                .extracting("nickname")
                .containsExactlyInAnyOrder("판매자1", "판매자2", "구매자1");
            assertThat(result).extracting("productInfo")
                .extracting("price", "productName", "saleState", "purchaser")
                .containsExactlyInAnyOrder(
                    tuple(1000,"나는짱물건1", SELLING, null),
                    tuple(2000,"나는짱물건2", SELLING, null),
                    tuple(3000,"나는짱물건3", SELLING, 1L)
                );
        }

        @DisplayName("내가 참여하고 있는 채팅방이 없을 시 빈 리스트를 반환한다.")
        @Test
        void getMyChatRoomList2() {
            User myAccount = createUser("본인", "midcon1@naver.com", "asd123123");
            User seller1 = createUser("판매자1", "midcon2@naver.com", "asd123123");
            User seller2 = createUser("판매자2", "midcon3@naver.com", "asd123123");
            User buyer1 = createUser("구매자1", "midcon4@naver.com", "asd123123");
            User buyer2 = createUser("구매자2", "midcon5@naver.com", "asd123123");
            userRepository.saveAll(List.of(myAccount, seller1, seller2, buyer1, buyer2));

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "강아지", "새상품");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "강아지", "새상품");
            productJpaRepository.saveAll(List.of(product1, product2));

            Long orderId1 = 1L;
            Long orderId2 = 2L;

            ChatRoom notMyChatRoom1 = createChatRoom(buyer1.getId(), seller1.getId(), orderId1, product1.getId());
            ChatRoom notMyChatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), orderId2, product2.getId());
            chatRoomRepository.saveAll(List.of(notMyChatRoom1, notMyChatRoom2));

            // when
            List<ChatRoomInfoResponse> result = chatRoomRepository.getMyChatRoomList(myAccount.getId());

            // then
            assertThat(result).hasSize(0);
        }
    }

    private static User createUser(String nickname, String email, String password) {
        return User.builder()
            .nickname(nickname)
            .email(email)
            .password(password)
            .build();
    }

    private Product createProduct(String name, int price, String category, String species, String condition) {
        return Product.builder()
            .name(name)
            .price(price)
            .category(category)
            .species(species)
            .condition(condition)
            .build();
    }

    private static ChatRoom createChatRoom(Long buyerId, Long sellerId, Long orderId, Long productId) {
        ChatRoom chatRoom = ChatRoom.builder()
            .buyerId(buyerId)
            .sellerId(sellerId)
            .orderId(orderId)
            .productId(productId)
            .build();
        return chatRoom;
    }
}
