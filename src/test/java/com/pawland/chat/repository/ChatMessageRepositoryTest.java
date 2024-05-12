package com.pawland.chat.repository;

import com.pawland.chat.domain.ChatMessage;
import com.pawland.chat.domain.ChatRoom;
import com.pawland.global.config.QueryDslConfig;
import com.pawland.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Import(QueryDslConfig.class)
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @DisplayName("채팅 내역 조회 시")
    @Nested
    class getChatMessageHistory1 {
        @DisplayName("채팅방 진입 시(messageTime == null)")
        @Nested
        class getChatMessageHistoryWithoutMessageTime {
            @DisplayName("전체 데이터 수가 pageSize 보다 크면 최근 생성 순서대로 pageSize 만큼 조회한다.")
            @Test
            void getChatMessageHistory1() {
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
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), null, 5
                );

                // then
                assertThat(result.size()).isEqualTo(5L);
                assertThat(result).extracting("roomId", "message", "senderId", "messageTime")
                    .containsExactly(
                        tuple(1L, "내용10", 2L, LocalDateTime.parse("2024-05-11T21:00:00.010")),
                        tuple(1L, "내용9", 1L, LocalDateTime.parse("2024-05-11T21:00:00.009")),
                        tuple(1L, "내용8", 2L, LocalDateTime.parse("2024-05-11T21:00:00.008")),
                        tuple(1L, "내용7", 1L, LocalDateTime.parse("2024-05-11T21:00:00.007")),
                        tuple(1L, "내용6", 2L, LocalDateTime.parse("2024-05-11T21:00:00.006"))
                    );
            }

            @DisplayName("전체 데이터 수가 pageSize와 같으면 최근 생성 순서대로 pageSize 만큼 조회한다.")
            @Test
            void getChatMessageHistory2() {
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
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), null, 5
                );

                // then
                assertThat(result.size()).isEqualTo(5L);
                assertThat(result).extracting("roomId", "message", "senderId", "messageTime")
                    .containsExactly(
                        tuple(1L, "내용5", 1L, LocalDateTime.parse("2024-05-11T21:00:00.005")),
                        tuple(1L, "내용4", 2L, LocalDateTime.parse("2024-05-11T21:00:00.004")),
                        tuple(1L, "내용3", 1L, LocalDateTime.parse("2024-05-11T21:00:00.003")),
                        tuple(1L, "내용2", 2L, LocalDateTime.parse("2024-05-11T21:00:00.002")),
                        tuple(1L, "내용1", 1L, LocalDateTime.parse("2024-05-11T21:00:00.001"))
                    );
            }

            @DisplayName("전체 데이터 수가 pageSize 보다 작을 때는 최근 생성 순서대로 데이터 수 만큼 조회한다.")
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
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), null, 5
                );

                // then
                assertThat(result.size()).isEqualTo(3L);
                assertThat(result).extracting("roomId", "message", "senderId", "messageTime")
                    .containsExactly(
                        tuple(1L, "내용3", 1L, LocalDateTime.parse("2024-05-11T21:00:00.003")),
                        tuple(1L, "내용2", 2L, LocalDateTime.parse("2024-05-11T21:00:00.002")),
                        tuple(1L, "내용1", 1L, LocalDateTime.parse("2024-05-11T21:00:00.001"))
                    );
            }

            @DisplayName("전체 데이터 수가 0이면 빈 리스트를 반환한다.")
            @Test
            void getChatMessageHistory4() {
                // given
                Long roomId = 1L;

                // when
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), null, 5
                );

                // then
                assertThat(result.size()).isEqualTo(0L);
            }
        }

        @DisplayName("이전 채팅 내역 조회 시(messageTime != null)")
        @Nested
        class getChatMessageHistoryWithMessageTime {
            @DisplayName("이전 채팅 데이터 수가 pageSize 보다 크면 최근 생성 순서대로 pageSize 만큼 조회한다.")
            @Test
            void getChatMessageHistory1() {
                // given
                Long roomId = 1L;
                Long userId1 = 1L;
                Long userId2 = 2L;
                List<ChatMessage> chatMessages = IntStream.rangeClosed(1, 7)
                    .mapToObj(i -> i % 2 == 1
                        ? createChatMessage(roomId, "내용" + i, userId1, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                        : createChatMessage(roomId, "내용" + i, userId2, "2024-05-11T21:00:00.0" + String.format("%02d", i))
                    )
                    .toList();
                chatMessageRepository.saveAll(chatMessages);

                // when
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.007", 5
                );

                // then
                assertThat(result.size()).isEqualTo(5L);
                assertThat(result).extracting("roomId", "message", "senderId", "messageTime")
                    .containsExactly(
                        tuple(1L, "내용6", 2L, LocalDateTime.parse("2024-05-11T21:00:00.006")),
                        tuple(1L, "내용5", 1L, LocalDateTime.parse("2024-05-11T21:00:00.005")),
                        tuple(1L, "내용4", 2L, LocalDateTime.parse("2024-05-11T21:00:00.004")),
                        tuple(1L, "내용3", 1L, LocalDateTime.parse("2024-05-11T21:00:00.003")),
                        tuple(1L, "내용2", 2L, LocalDateTime.parse("2024-05-11T21:00:00.002"))
                    );
            }

            @DisplayName("이전 채팅 데이터 수가 pageSize와 같으면 최근 생성 순서대로 pageSize 만큼 조회한다.")
            @Test
            void getChatMessageHistory2() {
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
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.006", 5
                );

                // then
                assertThat(result.size()).isEqualTo(5L);
                assertThat(result).extracting("roomId", "message", "senderId", "messageTime")
                    .containsExactly(
                        tuple(1L, "내용5", 1L, LocalDateTime.parse("2024-05-11T21:00:00.005")),
                        tuple(1L, "내용4", 2L, LocalDateTime.parse("2024-05-11T21:00:00.004")),
                        tuple(1L, "내용3", 1L, LocalDateTime.parse("2024-05-11T21:00:00.003")),
                        tuple(1L, "내용2", 2L, LocalDateTime.parse("2024-05-11T21:00:00.002")),
                        tuple(1L, "내용1", 1L, LocalDateTime.parse("2024-05-11T21:00:00.001"))
                    );
            }

            @DisplayName("이전 채팅 데이터 수가 pageSize 보다 작을 때는 최근 생성 순서대로 데이터 수 만큼 조회한다.")
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
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.004", 5
                );

                // then
                assertThat(result.size()).isEqualTo(3L);
                assertThat(result).extracting("roomId", "message", "senderId", "messageTime")
                    .containsExactly(
                        tuple(1L, "내용3", 1L, LocalDateTime.parse("2024-05-11T21:00:00.003")),
                        tuple(1L, "내용2", 2L, LocalDateTime.parse("2024-05-11T21:00:00.002")),
                        tuple(1L, "내용1", 1L, LocalDateTime.parse("2024-05-11T21:00:00.001"))
                    );
            }

            @DisplayName("이전 채팅 데이터 수가 0이면 빈 리스트를 반환한다.")
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
                List<ChatMessage> result = chatMessageRepository.getChatMessageHistory(
                    roomId.toString(), "2024-05-11T21:00:00.001", 5
                );

                // then
                assertThat(result.size()).isEqualTo(0L);
            }
        }
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
