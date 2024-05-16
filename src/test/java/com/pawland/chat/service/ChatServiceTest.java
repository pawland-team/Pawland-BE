package com.pawland.chat.service;

import com.pawland.chat.domain.ChatMessage;
import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.dto.request.ChatMessageRequest;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.dto.response.ChatMessageHistoryResponse;
import com.pawland.chat.dto.response.ChatMessageResponse;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.chat.repository.ChatMessageRepository;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.order.domain.Order;
import com.pawland.order.exception.OrderException;
import com.pawland.order.respository.OrderJpaRepository;
import com.pawland.product.domain.Product;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.pawland.product.domain.Status.SELLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

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
        orderJpaRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        chatMessageRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방 생성 시")
    @Nested
    class createChatRoom {
        @DisplayName("요청 값 관련 테스트")
        @Nested
        class createChatRoom2 {
            @DisplayName("요청 값에 판매자 ID, 상품 ID, 주문 ID가 모두 들어있으면 성공한다.")
            @Test
            void createChatRoom1() {
                // given
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                Order order = new Order(seller, buyer, product);
                orderJpaRepository.save(order);

                ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .orderId(order.getId())
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
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                Order order = new Order(seller, buyer, product);
                orderJpaRepository.save(order);

                ChatRoomCreateRequest requestWithoutSellerId = ChatRoomCreateRequest.builder()
                    .productId(product.getId())
                    .orderId(order.getId())
                    .build();

                ChatRoomCreateRequest requestWithoutProductId = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .orderId(order.getId())
                    .build();

                ChatRoomCreateRequest requestWithoutOrderId = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .build();

                List<ChatRoom> result = chatRoomRepository.findAll();

                // expected
                assertThat(result.size()).isEqualTo(0L);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutSellerId))
                    .isInstanceOf(RuntimeException.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutProductId))
                    .isInstanceOf(RuntimeException.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutOrderId))
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
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                Order order = new Order(seller, buyer, product);
                orderJpaRepository.save(order);

                ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .orderId(order.getId())
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
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                Order order = new Order(seller, buyer, product);
                orderJpaRepository.save(order);

                Long invalidSellerId = 0L;
                Long invalidProductId = 0L;
                Long invalidOrderId = 0L;

                ChatRoomCreateRequest requestWithInvalidSellerInfo = ChatRoomCreateRequest.builder()
                    .sellerId(invalidSellerId)
                    .productId(product.getId())
                    .orderId(order.getId())
                    .build();

                ChatRoomCreateRequest requestWithInvalidProductInfo = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(invalidProductId)
                    .orderId(order.getId())
                    .build();

                ChatRoomCreateRequest requestWithInvalidOrderInfo = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .orderId(invalidOrderId)
                    .build();

                List<ChatRoom> result = chatRoomRepository.findAll();

                // expected
                assertThat(result.size()).isEqualTo(0L);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidSellerInfo))
                    .isInstanceOf(UserException.NotFoundUser.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidProductInfo))
                    .isInstanceOf(ProductException.NotFoundProduct.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidOrderInfo))
                    .isInstanceOf(OrderException.NotFoundOrder.class);
            }
        }
    }

    @DisplayName("내 채팅방 목록 조회 시")
    @Nested
    class getMyChatRoomList {
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
                    tuple(1000, "나는짱물건1", SELLING, null),
                    tuple(2000, "나는짱물건2", SELLING, null),
                    tuple(3000, "나는짱물건3", SELLING, 1L)
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

    @DisplayName("채팅 내역 저장 시")
    @Nested
    class saveMessage {
        @DisplayName("발신자 ID와 내용이 빈 값이 아니면 성공한다.")
        @Test
        void saveMessage1() {
            // given
            ChatMessageRequest request = ChatMessageRequest.builder()
                .sender("1")
                .message("내용")
                .build();
            String roomId = "123";

            // when
            ChatMessageResponse result = chatService.saveMessage(roomId, request);
            List<ChatMessage> messageList = chatMessageRepository.findAll();

            // then
            assertThat(result).extracting("sender", "message")
                .containsExactlyInAnyOrder("1", "내용");
            assertThat(result.getMessageTime()).isNotBlank();
            assertThat(result.getMessageId()).isNotBlank();
            assertThat(messageList.size()).isEqualTo(1L);
        }

        @DisplayName("발신자 ID와 내용이 빈 값이 아니면 성공한다.")
        @Test
        void saveMessage2() {
            // given
            ChatMessageRequest requestWithoutSenderId = ChatMessageRequest.builder()
                .message("내용")
                .build();
            ChatMessageRequest requestWithoutMessage = ChatMessageRequest.builder()
                .sender("1")
                .build();
            ChatMessageRequest requestWithEmptyMessage = ChatMessageRequest.builder()
                .sender("1")
                .build();
            String roomId = "123";

            List<ChatMessage> messageList = chatMessageRepository.findAll();

            // expected
            assertThatThrownBy(() -> chatService.saveMessage(roomId, requestWithoutSenderId))
                .isInstanceOf(RuntimeException.class);
            assertThatThrownBy(() -> chatService.saveMessage(roomId, requestWithoutMessage))
                .isInstanceOf(RuntimeException.class);
            assertThatThrownBy(() -> chatService.saveMessage(roomId, requestWithEmptyMessage))
                .isInstanceOf(RuntimeException.class);
            assertThat(messageList.size()).isEqualTo(0L);
        }
    }

    @DisplayName("채팅 내역 조회 시")
    @Nested
    class getChatMessageHistory1 {
        @DisplayName("채팅방 진입 시(messageTime == null)")
        @Nested
        class getChatMessageHistoryWithoutMessageTime {
            @DisplayName("전체 데이터 수가 pageSize(10개)보다 크면 최근 생성 순서대로 pageSize 만큼 채팅 내역을 조회하고, nextCursor 값이 존재한다.")
            @Test
            void getChatMessageHistory1() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 11)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), null
                );

                // then
                assertThat(result.getNextCursor()).isEqualTo("2024-05-11T21:00:00.001");
                assertThat(result.getMessageList().size()).isEqualTo(10L);
                assertThat(result.getMessageList()).extracting("message", "sender", "messageTime")
                    .containsExactly(
                        tuple("내용11", "1", "2024-05-11T21:00:00.011"),
                        tuple("내용10", "2", "2024-05-11T21:00:00.010"),
                        tuple("내용9", "1", "2024-05-11T21:00:00.009"),
                        tuple("내용8", "2", "2024-05-11T21:00:00.008"),
                        tuple("내용7", "1", "2024-05-11T21:00:00.007"),
                        tuple("내용6", "2", "2024-05-11T21:00:00.006"),
                        tuple("내용5", "1", "2024-05-11T21:00:00.005"),
                        tuple("내용4", "2", "2024-05-11T21:00:00.004"),
                        tuple("내용3", "1", "2024-05-11T21:00:00.003"),
                        tuple("내용2", "2", "2024-05-11T21:00:00.002")
                    );
            }

            @DisplayName("전체 데이터 수가 pageSize(10개)와 같으면 최근 생성 순서대로 pageSize 만큼 조회하고 nextCursor 값은 null이다.")
            @Test
            void getChatMessageHistory2() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), null
                );

                // then
                assertThat(result.getNextCursor()).isNull();
                assertThat(result.getMessageList().size()).isEqualTo(10L);
                assertThat(result.getMessageList()).extracting("message", "sender", "messageTime")
                    .containsExactly(
                        tuple("내용10", "2", "2024-05-11T21:00:00.010"),
                        tuple("내용9", "1", "2024-05-11T21:00:00.009"),
                        tuple("내용8", "2", "2024-05-11T21:00:00.008"),
                        tuple("내용7", "1", "2024-05-11T21:00:00.007"),
                        tuple("내용6", "2", "2024-05-11T21:00:00.006"),
                        tuple("내용5", "1", "2024-05-11T21:00:00.005"),
                        tuple("내용4", "2", "2024-05-11T21:00:00.004"),
                        tuple("내용3", "1", "2024-05-11T21:00:00.003"),
                        tuple("내용2", "2", "2024-05-11T21:00:00.002"),
                        tuple("내용1", "1", "2024-05-11T21:00:00.001")
                    );
            }

            @DisplayName("전체 데이터 수가 pageSize(10개)보다 작으면 최근 생성 순서대로 데이터 수 만큼 조회하고, nextCursor 값은 null이다.")
            @Test
            void getChatMessageHistory3() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 3)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), null
                );

                // then
                assertThat(result.getNextCursor()).isNull();
                assertThat(result.getMessageList().size()).isEqualTo(3L);
                assertThat(result.getMessageList()).extracting("message", "sender", "messageTime")
                    .containsExactly(
                        tuple("내용3", "1", "2024-05-11T21:00:00.003"),
                        tuple("내용2", "2", "2024-05-11T21:00:00.002"),
                        tuple("내용1", "1", "2024-05-11T21:00:00.001")
                    );
            }

            @DisplayName("전체 데이터 수가 0이면 빈 리스트를 반환하고, nextCursor 값은 null이다.")
            @Test
            void getChatMessageHistory4() {
                // given
                Long roomId = 1L;

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), null
                );

                // then
                assertThat(result.getNextCursor()).isNull();
                assertThat(result.getMessageList().size()).isEqualTo(0L);
            }
        }

        @DisplayName("이전 채팅 내역 조회 시(messageTime != null)")
        @Nested
        class getChatMessageHistoryWithMessageTime {
            @DisplayName("이전 채팅 데이터 수가 pageSize(10개)보다 크면 최근 생성 순서대로 pageSize 만큼 채팅 내역을 조회하고, nextCursor 값이 존재한다.")
            @Test
            void getChatMessageHistory1() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 12)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.012"
                );

                // then
                assertThat(result.getNextCursor()).isEqualTo("2024-05-11T21:00:00.001");
                assertThat(result.getMessageList().size()).isEqualTo(10L);
                assertThat(result.getMessageList()).extracting("message", "sender", "messageTime")
                    .containsExactly(
                        tuple("내용11", "1", "2024-05-11T21:00:00.011"),
                        tuple("내용10", "2", "2024-05-11T21:00:00.010"),
                        tuple("내용9", "1", "2024-05-11T21:00:00.009"),
                        tuple("내용8", "2", "2024-05-11T21:00:00.008"),
                        tuple("내용7", "1", "2024-05-11T21:00:00.007"),
                        tuple("내용6", "2", "2024-05-11T21:00:00.006"),
                        tuple("내용5", "1", "2024-05-11T21:00:00.005"),
                        tuple("내용4", "2", "2024-05-11T21:00:00.004"),
                        tuple("내용3", "1", "2024-05-11T21:00:00.003"),
                        tuple("내용2", "2", "2024-05-11T21:00:00.002")
                    );
            }

            @DisplayName("이전 채팅 데이터 수가 pageSize(10개)와 같으면 최근 생성 순서대로 pageSize 만큼 조회하고 nextCursor 값은 null이다.")
            @Test
            void getChatMessageHistory2() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 11)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.011"
                );

                // then
                assertThat(result.getNextCursor()).isNull();
                assertThat(result.getMessageList().size()).isEqualTo(10L);
                assertThat(result.getMessageList()).extracting("message", "sender", "messageTime")
                    .containsExactly(
                        tuple("내용10", "2", "2024-05-11T21:00:00.010"),
                        tuple("내용9", "1", "2024-05-11T21:00:00.009"),
                        tuple("내용8", "2", "2024-05-11T21:00:00.008"),
                        tuple("내용7", "1", "2024-05-11T21:00:00.007"),
                        tuple("내용6", "2", "2024-05-11T21:00:00.006"),
                        tuple("내용5", "1", "2024-05-11T21:00:00.005"),
                        tuple("내용4", "2", "2024-05-11T21:00:00.004"),
                        tuple("내용3", "1", "2024-05-11T21:00:00.003"),
                        tuple("내용2", "2", "2024-05-11T21:00:00.002"),
                        tuple("내용1", "1", "2024-05-11T21:00:00.001")
                    );
            }

            @DisplayName("이전 채팅 데이터 수가 pageSize(10개)보다 작으면 최근 생성 순서대로 데이터 수 만큼 조회하고, nextCursor 값은 null이다.")
            @Test
            void getChatMessageHistory3() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.004"
                );

                // then
                assertThat(result.getNextCursor()).isNull();
                assertThat(result.getMessageList().size()).isEqualTo(3L);
                assertThat(result.getMessageList()).extracting("message", "sender", "messageTime")
                    .containsExactly(
                        tuple("내용3", "1", "2024-05-11T21:00:00.003"),
                        tuple("내용2", "2", "2024-05-11T21:00:00.002"),
                        tuple("내용1", "1", "2024-05-11T21:00:00.001")
                    );
            }

            @DisplayName("이전 채팅 데이터 수가 0이면 빈 리스트를 반환하고, nextCursor 값은 null이다.")
            @Test
            void getChatMessageHistory4() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                ChatMessageHistoryResponse result = chatService.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.001"
                );

                // then
                assertThat(result.getNextCursor()).isNull();
                assertThat(result.getMessageList().size()).isEqualTo(0L);
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

    private static ChatRoom createChatRoom(Long buyerId, Long sellerId, Long orderId, Long productId) {
        ChatRoom chatRoom = ChatRoom.builder()
            .buyerId(buyerId)
            .sellerId(sellerId)
            .orderId(orderId)
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
