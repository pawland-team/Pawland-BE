package com.pawland.chat.controller;

import com.pawland.chat.dto.request.ChatMessageRequest;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.dto.response.ChatMessageHistoryResponse;
import com.pawland.chat.dto.response.ChatMessageResponse;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.chat.service.ChatService;
import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.global.dto.ApiMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt-cookie")
@Tag(name = "ChatController", description = "채팅 관련 컨트롤러 입니다.")
public class ChatController {

    private final ChatService chatService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "내 채팅 목록 조회", description = "내 채팅 목록을 반환합니다.(미완성)")
    @ApiResponse(responseCode = "200", description = "채팅 목록 조회 성공")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/room")
    public ResponseEntity<List<ChatRoomInfoResponse>> getChatRoomList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ChatRoomInfoResponse> chatRoomList = chatService.getChatRoomList(userPrincipal.getUserId());
        return ResponseEntity
                .status(OK)
                .body(chatRoomList);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 상품 아이디 혹은 유저 아이디")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/room")
    public ResponseEntity<ApiMessageResponse> createChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @Valid @RequestBody ChatRoomCreateRequest request) {
        chatService.createChatRoom(userPrincipal.getUserId(), request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiMessageResponse("채팅방 생성 완료"));
    }

    @PreAuthorize("hasRole('ROLE_USER') && hasPermission(#roomId, 'CHATROOM', 'READ')")
    @Operation(summary = "해당 채팅방의 채팅 내역 조회", description = "해당 채팅방의 채팅 내역을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "채팅 내역 조회 성공")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/previous/{roomId}")
    public ResponseEntity<ChatMessageHistoryResponse> getPreviousChatMessage(@PathVariable String roomId,
                                                            @RequestParam(required = false) String messageTime) {
        ChatMessageHistoryResponse chatMessageHistory = chatService.getChatMessageHistory(roomId, messageTime);
        return ResponseEntity
            .status(OK)
            .body(chatMessageHistory);
    }

    @MessageMapping("/chat.sendMessage/{roomId}") // 프론트가 publish 할 때 사용할 백엔드 엔드포인트
    @SendTo("/topic/chatroom/{roomId}")   // 백엔드에서 프론트에 메시지를 보낼 구독 url
    public ChatMessageResponse sendMessage(@Valid @Payload ChatMessageRequest request, @DestinationVariable String roomId) {
        return chatService.saveMessage(roomId, request);
    }
}
