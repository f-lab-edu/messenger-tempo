package com.messenger.websocket;

import com.messenger.dto.chat.GroupChatResponse;
import com.messenger.dto.chat.GroupChatRoomResponse;
import com.messenger.dto.chat.SendGroupChatRequest;
import com.messenger.dto.member.MemberResponse;
import com.messenger.service.GroupChatService;
import com.messenger.service.MemberService;
import com.messenger.validator.GroupChatValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class GroupChatWebsocketController {

    private final MemberService memberService;
    private final GroupChatService groupChatService;
    private final GroupChatValidator groupChatValidator;
    private final SimpMessagingTemplate template;
    @Autowired private SimpUserRegistry userRegistry;
    @Autowired private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    @InitBinder
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(groupChatValidator);
    }

    public GroupChatWebsocketController(MemberService memberService, GroupChatService groupChatService, GroupChatValidator groupChatValidator, SimpMessagingTemplate template) {
        this.memberService = memberService;
        this.groupChatService = groupChatService;
        this.groupChatValidator = groupChatValidator;
        this.template = template;
    }

    @MessageMapping("/groupChat")
    public void sendGroupChat(@RequestBody SendGroupChatRequest request, Message<?> message, MessageHeaders headers) {
        /*
        GenericMessage [
            payload=byte[28],
            headers={
                simpMessageType=MESSAGE,
                stompCommand=SEND,
                nativeHeaders={
                    Authorization=[eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpZF90ZXN0NyIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2ODM4NzA1NjB9.hZ81_bbUEk6VQWSOAJO8-S2mtt1hNMRM-49gwHkRJKmLzODCBq1OGmKrD89xGSh9SPyTPK6CkGxcI83wfaui0A],
                    accept-version=[1.1,1.0],
                    heart-beat=[10000,10000],
                    destination=[/app/groupChat],
                    content-length=[28]
                },
                simpSessionAttributes={},
                simpHeartbeat=[J@33ef4bf2,
                simpUser=UsernamePasswordAuthenticationToken [
                    Principal=Member(id=id_test7, password=, name=undefined, statusMessage=, role=USER),
                    Credentials=[PROTECTED],
                    Authenticated=true,
                    Details=null,
                    Granted Authorities=[ROLE_USER]
                ],
                lookupDestination=/groupChat,
                simpSessionId=l13rlt5z,
                simpDestination=/app/groupChat
            }
        ] */
        log.debug("message = {}", message);
        printConnectedUsers();
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        Principal auth = SimpMessageHeaderAccessor.getUser(headers);
        SecurityContextHolder.getContext().setAuthentication((Authentication) auth);
        log.debug("sessionId = {}", sessionId);
        if (auth != null) {
            log.debug("auth = {}, auth.getName = {}", auth, auth.getName());
        } else {
            log.debug("auth = null");
        }
        log.debug("thread id = {}", Thread.currentThread().getId());
        log.debug("message received: "+request);
        GroupChatResponse groupChat = groupChatService.sendGroupChat(request);
        this.template.convertAndSend("/topic/room/"+request.getRoomId(), groupChat);
        sendAllUsersOfGroup(groupChat);
    }

    public void printConnectedUsers() {
        Set<SimpUser> currentUsers = userRegistry.getUsers();
        for (SimpUser currentUser : currentUsers) {
            log.debug("currentUser = {}", currentUser);
        }
        log.debug(webSocketMessageBrokerStats.getClientInboundExecutorStatsInfo());
    }

    public void sendAllUsersOfGroup(GroupChatResponse groupChat) {
        String content = groupChat.getContent();
        long roomId = groupChat.getRoomId();
        log.debug("roomId = {}, content = {}", roomId, content);
        List<String> memberIdList = groupChatService.listMemberIdByGroup(roomId);
        log.debug("memberIdList = {}", memberIdList);
        for (String memberId : memberIdList) {
            this.template.convertAndSendToUser(memberId, "/queue", groupChat);
        }
    }

    @RequestMapping("/")
    public String mainPage() {
        return "redirect:/login";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/chat-room")
    public String chatRoom(Model model, @RequestParam Long roomId) {
        Integer size = 10;
        List<GroupChatResponse> chatList = groupChatService.listChatByGroup(roomId, size);
        Collections.reverse(chatList);
        model.addAttribute("chatList", chatList);
        return "chat-room";
    }

    @RequestMapping("/chat-room-list")
    public String chatRoomList(Model model) {
        List<GroupChatRoomResponse> roomList = groupChatService.listGroupByUser();
        model.addAttribute("roomList", roomList);
        return "chat-room-list";
    }

    @RequestMapping("/make-room")
    public String makeRoom(Model model) {
        List<MemberResponse> memberList = memberService.listAll();
        model.addAttribute("memberList", memberList);
        return "make-room";
    }
}
