package com.messenger.web;

import com.messenger.domain.Chat;
import com.messenger.dto.DefaultResponse;
import com.messenger.dto.PaginationWrapper;
import com.messenger.service.ChatService;
import com.messenger.util.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @Parameters({
            @Parameter(name = "chatId", description = "채팅 메시지 id", required = true)})
    public DefaultResponse<Chat> getPersonalChat(
            @PathVariable long chatId) {

        Optional<Chat> chat = chatService.getPersonalChat(chatId);
        return DefaultResponse.ofSuccess(chat.orElse(null));
    }

    /**
     * 1:1 메시지를 전송
     * @param receiverUserId 수신 사용자 id
     * @param content    메시지 내용
     * @return 메시지 객체
     */
    @PostMapping("/api/v1/chat")
    @Operation(summary = "1:1 메시지 전송", security = {@SecurityRequirement(name = "authorization")})
    @Schema()
    @Parameters({
            @Parameter(name = "receiverUserId", description = "수신 사용자 id", required = true),
            @Parameter(name = "content", description = "메시지 내용", required = true)})
    public DefaultResponse<Chat> sendPersonalChat(
                @RequestParam String receiverUserId,
                @RequestParam String content) {

        Chat result = chatService.sendPersonalChat(receiverUserId, content);
        return DefaultResponse.ofSuccess(result);
    }

    /**
     * 자신이 전송한 1:1 메시지 하나를 삭제
     * @param chatId 메시지 id
     * @return "success"
     */
    @DeleteMapping("/api/v1/chat/{chatId}")
    @Operation(summary = "1:1 메시지 하나를 삭제",
            description = "자신이 전송한 1:1 메시지 하나를 삭제한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "chatId", description = "메시지 id", required = true)})
    public DefaultResponse<Void> deletePersonalChat(
                @PathVariable long chatId) {

        chatService.deletePersonalChat(chatId);
        return DefaultResponse.ofSuccess();
    }

    /**
     * (개발자용) 전체 1:1 메시지 목록 (최신순으로 조회)
     * @param nextId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat")
    @Operation(summary = "(개발자용) 전체 1:1 메시지 목록", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "prevId", description = "이전 조회한 마지막 메시지 id"),
            @Parameter(name = "size", description = "조회할 메시지 개수")})
    public DefaultResponse<PaginationWrapper<Chat>> listAllPersonalChat(
                @RequestParam(required = false) Integer nextId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> list = chatService.listAllPersonalChat(nextId, size);
        return DefaultResponse.ofSuccess(new PaginationWrapper<>(list));
    }

    /**
     * @deprecated
     * 자신이 전송한 모든 1:1 메시지의 목록 (최신순으로 조회)
     * @param nextId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/v1/chat/sent")
    @Operation(summary = "자신이 전송한 모든 1:1 메시지의 목록", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "prevId", description = "이전 조회한 마지막 메시지 id"),
            @Parameter(name = "size", description = "조회할 메시지 개수")})
    public DefaultResponse<PaginationWrapper<Chat>> listSentPersonalChat(
                @RequestParam(required = false) Integer nextId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> list = chatService.listPersonalChatBySender(nextId, size);
        return DefaultResponse.ofSuccess(new PaginationWrapper<>(list));
    }

    /**
     * 자신이 수신한 모든 1:1 메시지 목록 (최신순으로 조회)
     * @param nextId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/received")
    @Operation(summary = "자신이 수신한 모든 1:1 메시지 목록", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "prevId", description = "이전 조회한 마지막 메시지 id"),
            @Parameter(name = "size", description = "조회할 메시지 개수")})
    public DefaultResponse<PaginationWrapper<Chat>> listReceivedPersonalChat(
                @RequestParam(required = false) Integer nextId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> list = chatService.listPersonalChatByReceiver(nextId, size);
        return DefaultResponse.ofSuccess(new PaginationWrapper<>(list));
    }

    /**
     * 1:1 채팅방에 입장
     * 해당 방의 메시지 목록을 최신순으로 가져오고, 가장 최근 수신한 메시지를 읽음 표시한다
     * @param oppositeUserId 상대방 사용자 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트, 가장 최근 수신한 메시지
     */
    @GetMapping("/api/v1/chat/personal_chat/{oppositeUserId}/enter")
    @Operation(summary = "1:1 채팅방에 입장",
            description = "해당 그룹의 메시지 목록을 최신순으로 가져오고, 가장 최근 수신한 메시지를 읽음 표시한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "oppositeUserId", description = "상대방 사용자 id", required = true),
            @Parameter(name = "size", description = "조회할 메시지 개수")})
    public DefaultResponse<PaginationWrapper<Chat>> enterPersonalChatGroup(
                @PathVariable String oppositeUserId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        PaginationWrapper<Chat> result = chatService.enterPersonalChatGroup(oppositeUserId, size);
        return DefaultResponse.ofSuccess(result);
    }

    /**
     * 특정 1:1 채팅방의 메시지 목록 (최신순으로 조회)
     * 자신과 상대방의 사용자 id를 기준으로 검색한다
     * @param oppositeUserId 상대방 사용자 id
     * @param nextId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/personal_chat/{oppositeUserId}")
    @Operation(summary = "특정 1:1 채팅방의 메시지 목록",
            description = "자신과 상대방의 사용자 id를 기준으로 최신순으로 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "oppositeUserId", description = "상대방 사용자 id", required = true),
            @Parameter(name = "prevId", description = "이전 조회한 마지막 메시지 id"),
            @Parameter(name = "size", description = "조회할 메시지 개수")})
    public DefaultResponse<PaginationWrapper<Chat>> listPersonalChatByGroup(
                @PathVariable String oppositeUserId,
                @RequestParam(required = false) Integer nextId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> chatList = chatService.listPersonalChatByGroup(oppositeUserId, nextId, size);
        return DefaultResponse.ofSuccess(new PaginationWrapper<>(chatList));
    }

    /**
     * 개발자용
     */
    @GetMapping("/api/v1/chat/room/{userId}")
    @Operation(summary = "(개발자용) 1:1 채팅방 목록",
            description = "특정 유저가 포함되어 있는 모든 채팅방을 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id", required = true)})
    public DefaultResponse<List<Pair<String, Long>>> listGroupByUser(
                @PathVariable String userId) {
        List<Pair<String, Long>> result = chatService.listGroupByUser(userId);
        return DefaultResponse.ofSuccess(result);
    }

    @GetMapping("/api/v1/chat/room")
    @Operation(summary = "1:1 채팅방 목록",
            description = "자신이 포함되어 있는 모든 채팅방을 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    public DefaultResponse<List<Pair<String, Long>>> listGroupByUser() {
        List<Pair<String, Long>> result = chatService.listGroupByUser();
        return DefaultResponse.ofSuccess(result);
    }
}
