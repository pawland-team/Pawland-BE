package com.pawland.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.chat.domain.ChatMessage;
import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.repository.ChatMessageRepository;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.global.config.TestSecurityConfig;
import com.pawland.global.utils.PawLandMockUser;
import com.pawland.product.domain.Product;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import org.hamcrest.Matchers;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.pawland.product.exception.ProductExceptionMessage.PRODUCT_NOT_FOUND;
import static com.pawland.user.exception.UserExceptionMessage.USER_NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        productJpaRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        chatMessageRepository.deleteAllInBatch();
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

            Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
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
            Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
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

    @DisplayName("내 채팅방 목록 조회 시")
    @Nested
    class getMyChatRoomList {
        @DisplayName("내가 구매자인 채팅방과 판매자인 채팅방을 모두 조회한다.")
        @PawLandMockUser
        @Test
        void getMyChatRoomList1() throws Exception {
            // given
            User seller1 = createUser("판매자1", "midcon2@naver.com", "asd123123");
            User seller2 = createUser("판매자2", "midcon3@naver.com", "asd123123");
            User buyer1 = createUser("구매자1", "midcon4@naver.com", "asd123123");
            User buyer2 = createUser("구매자2", "midcon5@naver.com", "asd123123");
            userRepository.saveAll(List.of(seller1, seller2, buyer1, buyer2));

            User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "강아지", "새상품");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "강아지", "새상품");
            Product product3 = createProduct("나는짱물건3", 3000, "장난감", "강아지", "새상품");
            Product product4 = createProduct("나는짱물건4", 4000, "장난감", "강아지", "새상품");
            product3.confirmPurchase(1L);
            productJpaRepository.saveAll(List.of(product1, product2, product3, product4));

            ChatRoom myChatRoom1 = createChatRoom(myAccount.getId(), seller1.getId(), product1.getId());
            ChatRoom myChatRoom2 = createChatRoom(myAccount.getId(), seller2.getId(), product2.getId());
            ChatRoom myChatRoom3 = createChatRoom(buyer1.getId(), myAccount.getId(), product3.getId());
            ChatRoom notMyChatRoom1 = createChatRoom(buyer1.getId(), seller2.getId(), product4.getId());
            ChatRoom notMyChatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), product4.getId());
            chatRoomRepository.saveAll(List.of(myChatRoom1, myChatRoom2, myChatRoom3, notMyChatRoom1, notMyChatRoom2));

            // expected
            mockMvc.perform(get("/api/chat/rooms"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3));
        }

        @DisplayName("내가 참여하고 있는 채팅방이 없을 시 빈 리스트를 반환한다.")
        @PawLandMockUser
        @Test
        void getMyChatRoomList2() throws Exception {
            User seller1 = createUser("판매자1", "midcon2@naver.com", "asd123123");
            User seller2 = createUser("판매자2", "midcon3@naver.com", "asd123123");
            User buyer1 = createUser("구매자1", "midcon4@naver.com", "asd123123");
            User buyer2 = createUser("구매자2", "midcon5@naver.com", "asd123123");
            userRepository.saveAll(List.of(seller1, seller2, buyer1, buyer2));

            User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "강아지", "새상품");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "강아지", "새상품");
            productJpaRepository.saveAll(List.of(product1, product2));

            ChatRoom notMyChatRoom1 = createChatRoom(buyer1.getId(), seller1.getId(), product1.getId());
            ChatRoom notMyChatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), product2.getId());
            chatRoomRepository.saveAll(List.of(notMyChatRoom1, notMyChatRoom2));

            // expected
            mockMvc.perform(get("/api/chat/rooms"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @DisplayName("채팅 내역 조회 시")
    @PawLandMockUser
    @Nested
    class getPreviousChatMessage {
        @DisplayName("채팅방 진입 시(messageTime == null)")
        @Nested
        class getPreviousChatMessageWithoutMessageTime {
            @DisplayName("전체 데이터 수가 pageSize(10개)보다 크면 최근 생성 순서대로 pageSize 만큼 채팅 내역을 조회하고, nextCursor 값이 존재한다.")
            @Test
            void getPreviousChatMessage1() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 11)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor").value("2024-05-11T21:00:00.001"))
                    .andExpect(jsonPath("$.messageList.length()").value(10L))
                    .andExpect(jsonPath("$.messageList[0].messageTime").value("2024-05-11T21:00:00.011"))
                    .andExpect(jsonPath("$.messageList[9].messageTime").value("2024-05-11T21:00:00.002"));
            }

            @DisplayName("전체 데이터 수가 pageSize(10개)와 같으면 최근 생성 순서대로 pageSize 만큼 조회하고 nextCursor 값은 null이다.")
            @Test
            void getPreviousChatMessage2() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor", Matchers.nullValue()))
                    .andExpect(jsonPath("$.messageList.length()").value(10L))
                    .andExpect(jsonPath("$.messageList[0].messageTime").value("2024-05-11T21:00:00.010"))
                    .andExpect(jsonPath("$.messageList[9].messageTime").value("2024-05-11T21:00:00.001"));
            }

            @DisplayName("전체 데이터 수가 pageSize(10개)보다 작으면 최근 생성 순서대로 데이터 수 만큼 조회하고, nextCursor 값은 null이다.")
            @Test
            void getPreviousChatMessage3() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor", Matchers.nullValue()))
                    .andExpect(jsonPath("$.messageList.length()").value(5L))
                    .andExpect(jsonPath("$.messageList[0].messageTime").value("2024-05-11T21:00:00.005"))
                    .andExpect(jsonPath("$.messageList[4].messageTime").value("2024-05-11T21:00:00.001"));
            }

            @DisplayName("전체 데이터 수가 0이면 빈 리스트를 반환하고, nextCursor 값은 null이다.")
            @Test
            void getPreviousChatMessage4() throws Exception {
                // given
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor", Matchers.nullValue()))
                    .andExpect(jsonPath("$.messageList.length()").value(0L));
            }
        }

        @DisplayName("이전 채팅 내역 조회 시(messageTime != null)")
        @Nested
        class getPreviousChatMessageWithMessageTime {
            @DisplayName("이전 채팅 데이터 수가 pageSize(10개)보다 크면 최근 생성 순서대로 pageSize 만큼 채팅 내역을 조회하고, nextCursor 값이 존재한다.")
            @Test
            void getPreviousChatMessage1() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 12)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId())
                        .param("messageTime", "2024-05-11T21:00:00.012"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor").value("2024-05-11T21:00:00.001"))
                    .andExpect(jsonPath("$.messageList.length()").value(10L))
                    .andExpect(jsonPath("$.messageList[0].messageTime").value("2024-05-11T21:00:00.011"))
                    .andExpect(jsonPath("$.messageList[9].messageTime").value("2024-05-11T21:00:00.002"));
            }

            @DisplayName("이전 채팅 데이터 수가 pageSize(10개)와 같으면 최근 생성 순서대로 pageSize 만큼 조회하고 nextCursor 값은 null이다.")
            @Test
            void getPreviousChatMessage2() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 11)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId())
                        .param("messageTime", "2024-05-11T21:00:00.011"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor", Matchers.nullValue()))
                    .andExpect(jsonPath("$.messageList.length()").value(10L))
                    .andExpect(jsonPath("$.messageList[0].messageTime").value("2024-05-11T21:00:00.010"))
                    .andExpect(jsonPath("$.messageList[9].messageTime").value("2024-05-11T21:00:00.001"));
            }

            @DisplayName("이전 채팅 데이터 수가 pageSize(10개)보다 작으면 최근 생성 순서대로 데이터 수 만큼 조회하고, nextCursor 값은 null이다.")
            @Test
            void getPreviousChatMessage3() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId())
                        .param("messageTime", "2024-05-11T21:00:00.004"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor", Matchers.nullValue()))
                    .andExpect(jsonPath("$.messageList.length()").value(3L))
                    .andExpect(jsonPath("$.messageList[0].messageTime").value("2024-05-11T21:00:00.003"))
                    .andExpect(jsonPath("$.messageList[2].messageTime").value("2024-05-11T21:00:00.001"));
            }

            @DisplayName("이전 채팅 데이터 수가 0이면 빈 리스트를 반환하고, nextCursor 값은 null이다.")
            @Test
            void getPreviousChatMessage4() throws Exception {
                // given
                User myAccount = userRepository.findByEmail("midcondria@naver.com")
                    .orElseThrow(UserException.NotFoundUser::new);
                Long opponentUserId = 2L;
                Long productId = 1L;

                ChatRoom chatRoom = createChatRoom(myAccount.getId(), opponentUserId, productId);
                chatRoomRepository.save(chatRoom);

                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(chatRoom.getId(), "내용" + i, myAccount.getId(), "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(chatRoom.getId(), "내용" + i, opponentUserId, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // expected
                mockMvc.perform(get("/api/chat/previous/{roomId}", chatRoom.getId())
                        .param("messageTime", "2024-05-11T21:00:00.001"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nextCursor", Matchers.nullValue()))
                    .andExpect(jsonPath("$.messageList.length()").value(0L));
            }
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

    private static ChatMessage createChatMessage(Long roomId, String message, Long senderId, String messageTime) {
        return ChatMessage.builder()
            .roomId(roomId)
            .message(message)
            .senderId(senderId)
            .messageTime(LocalDateTime.parse(messageTime))
            .build();
    }
}
