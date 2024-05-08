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

import java.util.List;

import static com.pawland.product.domain.Status.SELLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Import(QueryDslConfig.class)
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
            userRepository.saveAll(List.of(myAccount, seller1, seller2, buyer1));

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "DOG", "NEW");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "DOG", "NEW");
            Product product3 = createProduct("나는짱물건3", 3000, "장난감", "DOG", "NEW");
            product3.confirmPurchase(1L);
            productJpaRepository.saveAll(List.of(product1, product2, product3));

            ChatRoom chatRoom1 = createChatRoom(myAccount.getId(), seller1.getId(), product1.getId());
            ChatRoom chatRoom2 = createChatRoom(myAccount.getId(), seller2.getId(), product2.getId());
            ChatRoom chatRoom3 = createChatRoom(buyer1.getId(), myAccount.getId(), product3.getId());
            chatRoomRepository.saveAll(List.of(chatRoom1, chatRoom2, chatRoom3));

            // when
            List<ChatRoomInfoResponse> chatRoomList = chatRoomRepository.getMyChatRoomList(myAccount.getId());

            // then
            assertThat(chatRoomList).hasSize(3);
            assertThat(chatRoomList).extracting("opponentUser")
                .extracting("nickname")
                .containsExactlyInAnyOrder("판매자1", "판매자2", "구매자1");
            assertThat(chatRoomList).extracting("productInfo")
                .extracting("price", "productName", "saleState", "purchaser")
                .containsExactlyInAnyOrder(
                    tuple(1000,"나는짱물건1", SELLING, null),
                    tuple(2000,"나는짱물건2", SELLING, null),
                    tuple(3000,"나는짱물건3", SELLING, 1L)
                );

            List<ChatRoom> all = chatRoomRepository.findAll();
            System.out.println("[size] " + all.size());
            System.out.println("[size] " + all.get(0).getId());

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

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "DOG", "NEW");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "DOG", "NEW");
            productJpaRepository.saveAll(List.of(product1, product2));

            ChatRoom chatRoom1 = createChatRoom(buyer1.getId(), seller1.getId(), product1.getId());
            ChatRoom chatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), product2.getId());
            chatRoomRepository.saveAll(List.of(chatRoom1, chatRoom2));

            // when
            List<ChatRoomInfoResponse> chatRoomList = chatRoomRepository.getMyChatRoomList(myAccount.getId());

            // then
            assertThat(chatRoomList).hasSize(0);
            List<ChatRoom> all = chatRoomRepository.findAll();
            System.out.println("[size] " + all.size());
            System.out.println("[size] " + all.get(0).getId());
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

    private static ChatRoom createChatRoom(Long buyerId, Long sellerId, Long productId) {
        ChatRoom chatRoom = ChatRoom.builder()
            .buyerId(buyerId)
            .sellerId(sellerId)
            .productId(productId)
            .build();
        return chatRoom;
    }
}
