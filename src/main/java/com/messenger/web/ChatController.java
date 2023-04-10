package com.messenger.web;

import com.messenger.domain.Chat;
import com.messenger.domain.PaginationWrapper;
import com.messenger.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 1:1 메시지를 전송
     * @param receiverUserId 수신 사용자 id
     * @param content    메시지 내용
     * @return 메시지 객체
     */
    @PostMapping("/api/v1/chat")
    public ResponseEntity<Chat> sendPersonalChat(
                @RequestParam String receiverUserId,
                @RequestParam String content) {

        Chat ret = chatService.sendPersonalChat(receiverUserId, content);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * 자신이 전송한 1:1 메시지 하나를 삭제
     * @param chatId 메시지 id
     * @return "success"
     */
    @DeleteMapping("/api/v1/chat/{chatId}")
    public ResponseEntity<String> deletePersonalChat(
                @PathVariable long chatId) {

        chatService.deletePersonalChat(chatId);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    /**
     * (개발자용) 전체 1:1 메시지 목록
     * @param prevId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat")
    public ResponseEntity<PaginationWrapper> listAllPersonalChat(
                @RequestParam(required = false) Integer prevId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> list = chatService.listAllPersonalChat(prevId, size);
        return new ResponseEntity<>(new PaginationWrapper(list), HttpStatus.OK);
    }

    /**
     * 자신이 전송한 모든 1:1 메시지의 목록
     * @param prevId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @param session 세션
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/sent")
    public ResponseEntity<PaginationWrapper> listSentPersonalChat(
                @RequestParam(required = false) Integer prevId,
                @RequestParam(required = false, defaultValue = "3") Integer size,
                HttpSession session) {

        List<Chat> list = chatService.listPersonalChatBySender(prevId, size);
        return new ResponseEntity<>(new PaginationWrapper(list), HttpStatus.OK);
    }

    /**
     * 자신이 수신한 모든 1:1 메시지 목록
     * @param prevId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/received")
    public ResponseEntity<PaginationWrapper> listReceivedPersonalChat(
                @RequestParam(required = false) Integer prevId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> list = chatService.listPersonalChatByReceiver(prevId, size);
        return new ResponseEntity<>(new PaginationWrapper(list), HttpStatus.OK);
    }

    /**
     * 1:1 채팅 그룹에 입장
     * 해당 그룹의 메시지 목록을 최신순으로 가져오고, 가장 최근 수신한 메시지를 읽음 표시한다
     * @param oppositeUserId 상대방 사용자 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트, 가장 최근 수신한 메시지
     */
    @GetMapping("/api/v1/chat/personal_chat/{oppositeUserId}/enter")
    public ResponseEntity<PaginationWrapper> enterPersonalChatGroup(
                @PathVariable String oppositeUserId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        PaginationWrapper result = chatService.enterPersonalChatGroup(oppositeUserId, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 특정 1:1 채팅 그룹의 메시지 목록 (최신순)
     * 자신과 상대방의 사용자 id를 기준으로 검색한다
     * @param oppositeUserId 상대방 사용자 id
     * @param prevId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/personal_chat/{oppositeUserId}")
    public ResponseEntity<PaginationWrapper> listPersonalChatByGroup(
                @PathVariable String oppositeUserId,
                @RequestParam(required = false) Integer prevId,
                @RequestParam(required = false, defaultValue = "3") Integer size) {

        List<Chat> chatList = chatService.listPersonalChatByGroup(oppositeUserId, prevId, size);
        return new ResponseEntity<>(new PaginationWrapper(chatList), HttpStatus.OK);
    }
}
