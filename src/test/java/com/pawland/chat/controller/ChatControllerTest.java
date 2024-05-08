package com.pawland.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.global.config.TestSecurityConfig;
import com.pawland.global.utils.PawLandMockUser;
import com.pawland.product.domain.Product;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.pawland.product.exception.ProductExceptionMessage.PRODUCT_NOT_FOUND;
import static com.pawland.user.exception.UserExceptionMessage.USER_NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        @DisplayName("유효한 요청이면 채팅방 생성에 성공한다.")
        @PawLandMockUser
        @Test
        void createChatRoom1() throws Exception {
            // given
            User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
            userRepository.save(seller);

            Product product = createProduct("나는짱물건", 10000, "장난감", "DOG", "NEW");
            productJpaRepository.save(product);

            ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .sellerId(seller.getId())
                .productId(product.getId())
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/chat/rooms")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("채팅방 생성 완료"));
        }

        @DisplayName("요청한 상품 ID로 DB에서 상품 정보를 조회할 수 없으면 에러 메시지를 출력한다.")
        @PawLandMockUser
        @Test
        void createChatRoom2() throws Exception {
            // given
            User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
            userRepository.save(seller);

            Long invalidProductId = 0L;

            ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .sellerId(seller.getId())
                .productId(invalidProductId)
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/chat/rooms")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PRODUCT_NOT_FOUND.getMessage()));
        }

        @DisplayName("요청한 판매자 ID로 DB에서 판매자 정보를 조회할 수 없으면 에러 메시지를 출력한다.")
        @PawLandMockUser
        @Test
        void createChatRoom3() throws Exception {
            // given
            Product product = createProduct("나는짱물건", 10000, "장난감", "DOG", "NEW");
            productJpaRepository.save(product);

            Long invalidSellerId = 0L;

            ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .sellerId(invalidSellerId)
                .productId(product.getId())
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/chat/rooms")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(USER_NOT_FOUND.getMessage()));
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
