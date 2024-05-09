package com.pawland.chat.controller;

import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.chat.service.ChatService;
import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.global.dto.ApiMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "내 채팅 목록 조회", description = "내 채팅 목록을 반환합니다.(미완성)")
    @ApiResponse(responseCode = "200", description = "채팅 목록 조회 성공")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomInfoResponse>> getChatRoomList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ChatRoomInfoResponse> chatRoomList = chatService.getChatRoomList(userPrincipal.getUserId());
        return ResponseEntity
            .status(OK)
            .body(chatRoomList);
    }

    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 상품 아이디 유저 아이디")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/rooms")
    public ResponseEntity<ApiMessageResponse> createChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @Valid @RequestBody ChatRoomCreateRequest request) {
        chatService.createChatRoom(userPrincipal.getUserId(), request);
        return ResponseEntity
            .status(CREATED)
            .body(new ApiMessageResponse("채팅방 생성 완료"));
    }
}
