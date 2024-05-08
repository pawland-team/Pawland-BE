package com.pawland.chat.service;

import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.product.domain.Product;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        productJpaRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방 생성 시")
    @Nested
    class createChatRoom {
        @DisplayName("요청 값 관련 테스트")
        @Nested
        class createChatRoom2 {
            @DisplayName("요청 값에 구매자 ID, 판매자 ID, 상품 ID가 모두 들어있으면 성공한다.")
            @Test
            void createChatRoom1() {
                // given
                User buyer = User.builder()
                    .nickname("구매자")
                    .email("midcon1@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(buyer);

                User seller = User.builder()
                    .nickname("판매자")
                    .email("midcon2@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(seller);

                Product product = Product.builder()
                    .name("나는짱물건")
                    .price(10000)
                    .category("장난감")
                    .species("DOG")
                    .condition("NEW")
                    .build();
                productJpaRepository.save(product);

                ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .build();

                // when
                chatService.createChatRoom(buyer.getId(), request);
                List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
                ChatRoom result = chatRoomList.get(0);

                // then
                assertThat(chatRoomList.size()).isEqualTo(1L);
                assertThat(result.getBuyerId()).isEqualTo(buyer.getId());
                assertThat(result.getSellerId()).isEqualTo(seller.getId());
                assertThat(result.getProductId()).isEqualTo(product.getId());
            }

            @DisplayName("요청 값에 구매자, 판매자, 상품의 ID 중 하나라도 없으면 예외를 던진다.")
            @Test
            void createChatRoom2() {
                // given
                User buyer = User.builder()
                    .nickname("구매자")
                    .email("midcon1@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(buyer);

                User seller = User.builder()
                    .nickname("판매자")
                    .email("midcon2@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(seller);

                Product product = Product.builder()
                    .name("나는짱물건")
                    .price(10000)
                    .category("장난감")
                    .species("DOG")
                    .condition("NEW")
                    .build();
                productJpaRepository.save(product);

                ChatRoomCreateRequest requestWithoutSellerId = ChatRoomCreateRequest.builder()
                    .productId(product.getId())
                    .build();

                ChatRoomCreateRequest requestWithoutProductId = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .build();

                List<ChatRoom> result = chatRoomRepository.findAll();

                // expected
                assertThat(result.size()).isEqualTo(0L);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutSellerId))
                    .isInstanceOf(RuntimeException.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutProductId))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @DisplayName("DB 정보 관련 테스트")
        @Nested
        class createChatRoom1 {
            @DisplayName("요청 값에 담긴 정보로 DB에서 판매자, 상품 정보 조회가 가능하면 성공한다.")
            @Test
            void createChatRoom1() {
                // given
                User buyer = User.builder()
                    .nickname("구매자")
                    .email("midcon1@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(buyer);

                User seller = User.builder()
                    .nickname("판매자")
                    .email("midcon2@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(seller);

                Product product = Product.builder()
                    .name("나는짱물건")
                    .price(10000)
                    .category("장난감")
                    .species("DOG")
                    .condition("NEW")
                    .build();
                productJpaRepository.save(product);

                ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .build();

                // when
                chatService.createChatRoom(buyer.getId(), request);
                List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
                ChatRoom result = chatRoomList.get(0);

                // then
                assertThat(chatRoomList.size()).isEqualTo(1L);
                assertThat(result.getBuyerId()).isEqualTo(buyer.getId());
                assertThat(result.getSellerId()).isEqualTo(seller.getId());
                assertThat(result.getProductId()).isEqualTo(product.getId());
            }

            @DisplayName("요청 값에 담긴 정보로 DB에서 판매자, 상품 정보 중 하나라도 조회하지 못하면 예외를 던진다.")
            @Test
            void createChatRoom2() {
                // given
                User buyer = User.builder()
                    .nickname("구매자")
                    .email("midcon1@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(buyer);

                User seller = User.builder()
                    .nickname("판매자")
                    .email("midcon2@nav.com")
                    .password("asd123123")
                    .build();
                userRepository.save(seller);

                Product product = Product.builder()
                    .name("나는짱물건")
                    .price(10000)
                    .category("장난감")
                    .species("DOG")
                    .condition("NEW")
                    .build();
                productJpaRepository.save(product);

                Long InvalidSellerId = 0L;
                Long InvalidProductId = 0L;

                ChatRoomCreateRequest requestWithInvalidSellerInfo = ChatRoomCreateRequest.builder()
                    .sellerId(InvalidSellerId)
                    .productId(product.getId())
                    .build();

                ChatRoomCreateRequest requestWithInvalidProductInfo = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(InvalidProductId)
                    .build();

                List<ChatRoom> result = chatRoomRepository.findAll();

                // expected
                assertThat(result.size()).isEqualTo(0L);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidSellerInfo))
                    .isInstanceOf(UserException.NotFoundUser.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidProductInfo))
                    .isInstanceOf(ProductException.NotFoundProduct.class);
            }
        }
    }
}