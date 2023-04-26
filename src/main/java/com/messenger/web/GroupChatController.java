package com.messenger.web;

import com.messenger.domain.GroupChat;
import com.messenger.dto.DefaultResponse;
import com.messenger.dto.chat.MakeNewGroupRequest;
import com.messenger.dto.chat.SendGroupChatRequest;
import com.messenger.dto.chat.GroupChatRoomResponse;
import com.messenger.dto.pagination.PaginationRequest;
import com.messenger.dto.pagination.PaginationResponse;
import com.messenger.service.GroupChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class GroupChatController {

    private final GroupChatService groupChatService;

    public GroupChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @GetMapping("/api/v1/groupchat/{chatId}")
    @Operation(summary = "그룹 채팅 메시지 하나를 조회",
            description = "채팅 메시지 id 기반으로 그룹 채팅 메시지 하나를 조회",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "chatId", description = "채팅 메시지 id", required = true)
    public GroupChat getPersonalChat(@PathVariable long chatId) {

        return groupChatService.getGroupChat(chatId).orElse(null);
    }

    @PostMapping("/api/v1/groupchat")
    @Operation(summary = "그룹 메시지 전송", security = {@SecurityRequirement(name = "authorization")})
    public GroupChat sendPersonalChat(@RequestBody SendGroupChatRequest request) {

        return groupChatService.sendGroupChat(request);
    }

    @DeleteMapping("/api/v1/groupchat/{chatId}")
    @Operation(summary = "그룹 메시지 하나를 삭제",
            description = "자신이 전송한 그룹 메시지 하나를 삭제한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "chatId", description = "메시지 id", required = true)
    public DefaultResponse deletePersonalChat(@PathVariable long chatId) {

        groupChatService.deletePersonalChat(chatId);
        return DefaultResponse.ofSuccess();
    }

    @GetMapping("/api/v1/groupchat/received")
    @Operation(summary = "자신이 수신한 모든 그룹 메시지 목록", security = {@SecurityRequirement(name = "authorization")})
    public PaginationResponse<GroupChat> listReceivedPersonalChat(@ModelAttribute PaginationRequest request) {

        List<GroupChat> list = groupChatService.listPersonalChatByReceiver(request);
        return PaginationResponse.of(list);
    }

    @GetMapping("/api/v1/groupchat/rooms/{roomId}/enter")
    @Operation(summary = "그룹 채팅방에 입장",
            description = "해당 그룹의 메시지 목록을 최신순으로 가져오고, 가장 최근 수신한 메시지를 읽음 표시한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "oppositeUserId", description = "상대방 사용자 id", required = true)
    @Parameter(name = "size", description = "조회할 메시지 개수")
    public PaginationResponse<GroupChat> enterGroupChat(
            @PathVariable Long roomId,
            @RequestParam(required = false, defaultValue = "3") Integer size) {

        return groupChatService.enterGroupChat(roomId, size);
    }

    @GetMapping("/api/v1/groupchat/{roomId}")
    @Operation(summary = "특정 그룹 채팅방의 메시지 목록",
            description = "자신과 상대방의 사용자 id를 기준으로 최신순으로 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "oppositeUserId", description = "상대방 사용자 id", required = true)
    public PaginationResponse<GroupChat> listPersonalChatByGroup(
            @PathVariable Long roomId,
            @ModelAttribute PaginationRequest request) {

        List<GroupChat> chatList = groupChatService.listChatByGroup(roomId, request);
        return PaginationResponse.of(chatList);
    }

    @GetMapping("/api/v1/groupchat/rooms/{userId}")
    @Operation(summary = "(개발자용) 그룹 채팅방 목록",
            description = "특정 유저가 포함되어 있는 모든 채팅방을 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "userId", description = "사용자 id", required = true)
    public List<GroupChatRoomResponse> listGroupByUser(@PathVariable String userId) {

        return groupChatService.listGroupByUser(userId);
    }

    @GetMapping("/api/v1/groupchat/rooms")
    @Operation(summary = "그룹 채팅방 목록",
            description = "자신이 포함되어 있는 모든 채팅방을 검색한다",
            security = {@SecurityRequirement(name = "authorization")})
    public List<GroupChatRoomResponse> listGroupByUser() {

        return groupChatService.listGroupByUser();
    }

    @PostMapping("/api/v1/groupchat/rooms")
    @Operation(summary = "그룹 채팅방 만들기",
            security = {@SecurityRequirement(name = "authorization")})
    public List<String> makeNewGroup(@RequestBody MakeNewGroupRequest request) {

        return groupChatService.makeNewGroup(request);
    }

}
