package com.messenger.web;

import com.messenger.domain.Chat;
import com.messenger.dto.DefaultResponse;
import com.messenger.dto.pagination.PaginationRequest;
import com.messenger.dto.pagination.PaginationResponse;
import com.messenger.dto.chat.ChatRequestSendPersonalChat;
import com.messenger.service.ChatService;
import com.messenger.util.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/api/v1/chat/{chatId}")
    @Operation(summary = "1:1 채팅 메시지 하나를 조회",
            description = "채팅 메시지 id 기반으로 1:1 채팅 메시지 하나를 조회",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "chatId", description = "채팅 메시지 id", required = true)
    public Chat getPersonalChat(@PathVariable long chatId) {

        return chatService.getPersonalChat(chatId).orElse(null);
    }

    @PostMapping("/api/v1/chat")
    @Operation(summary = "1:1 메시지 전송", security = {@SecurityRequirement(name = "authorization")})
    public Chat sendPersonalChat(@RequestBody ChatRequestSendPersonalChat request) {

        return chatService.sendPersonalChat(request);
    }

    @DeleteMapping("/api/v1/chat/{chatId}")
    @Operation(summary = "1:1 메시지 하나를 삭제",
            description = "자신이 전송한 1:1 메시지 하나를 삭제한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "chatId", description = "메시지 id", required = true)
    public DefaultResponse deletePersonalChat(@PathVariable long chatId) {

        chatService.deletePersonalChat(chatId);
        return DefaultResponse.ofSuccess();
    }

    @GetMapping("/api/v1/chat")
    @Operation(summary = "(개발자용) 전체 1:1 메시지 목록", security = {@SecurityRequirement(name = "authorization")})
    public PaginationResponse<Chat> listAllPersonalChat(@RequestBody PaginationRequest request) {

        List<Chat> list = chatService.listAllPersonalChat(request);
        return PaginationResponse.of(list);
    }

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/v1/chat/sent")
    @Operation(summary = "자신이 전송한 모든 1:1 메시지의 목록", security = {@SecurityRequirement(name = "authorization")})
    public PaginationResponse<Chat> listSentPersonalChat(@RequestBody PaginationRequest request) {

        List<Chat> list = chatService.listPersonalChatBySender(request);
        return PaginationResponse.of(list);
    }

    @GetMapping("/api/v1/chat/received")
    @Operation(summary = "자신이 수신한 모든 1:1 메시지 목록", security = {@SecurityRequirement(name = "authorization")})
    public PaginationResponse<Chat> listReceivedPersonalChat(@RequestBody PaginationRequest request) {

        List<Chat> list = chatService.listPersonalChatByReceiver(request);
        return PaginationResponse.of(list);
    }

    @GetMapping("/api/v1/chat/personal_chat/{oppositeUserId}/enter")
    @Operation(summary = "1:1 채팅방에 입장",
            description = "해당 그룹의 메시지 목록을 최신순으로 가져오고, 가장 최근 수신한 메시지를 읽음 표시한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "oppositeUserId", description = "상대방 사용자 id", required = true)
    @Parameter(name = "size", description = "조회할 메시지 개수")
    public PaginationResponse<Chat> enterPersonalChatGroup(
            @PathVariable String oppositeUserId,
            @RequestParam(required = false, defaultValue = "3") Integer size) {

        return chatService.enterPersonalChatGroup(oppositeUserId, size);
    }

    @GetMapping("/api/v1/chat/personal_chat/{oppositeUserId}")
    @Operation(summary = "특정 1:1 채팅방의 메시지 목록",
            description = "자신과 상대방의 사용자 id를 기준으로 최신순으로 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "oppositeUserId", description = "상대방 사용자 id", required = true)
    public PaginationResponse<Chat> listPersonalChatByGroup(
            @PathVariable String oppositeUserId,
            @RequestBody PaginationRequest request) {

        List<Chat> chatList = chatService.listPersonalChatByGroup(oppositeUserId, request);
        return PaginationResponse.of(chatList);
    }

    @GetMapping("/api/v1/chat/room/{userId}")
    @Operation(summary = "(개발자용) 1:1 채팅방 목록",
            description = "특정 유저가 포함되어 있는 모든 채팅방을 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "userId", description = "사용자 id", required = true)
    public List<Pair<String, Long>> listGroupByUser(@PathVariable String userId) {

        return chatService.listGroupByUser(userId);
    }

    @GetMapping("/api/v1/chat/room")
    @Operation(summary = "1:1 채팅방 목록",
            description = "자신이 포함되어 있는 모든 채팅방을 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    public List<Pair<String, Long>> listGroupByUser() {

        return chatService.listGroupByUser();
    }
}
