package com.messenger.web;

import com.messenger.domain.Chat;
import com.messenger.service.ChatService;
import lombok.NonNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 메시지를 전송
     * @param receiverUserId 수신 사용자 id
     * @param content    메시지 내용
     * @param session    세션
     * @return 메시지 객체
     */
    @PostMapping("/api/v1/chat")
    public ResponseEntity<Chat> sendPersonalChat(@RequestParam String receiverUserId,
                                                 @RequestParam String content,
                                                 @NonNull HttpSession session) {
        String sessionUserId = getSessionUserId(session);
        if (sessionUserId == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Chat ret;
        try {
            ret = chatService.sendPersonalChat(
                    Chat.builder()
                            .senderUserId(sessionUserId)
                            .receiverUserId(receiverUserId)
                            .content(content)
                            .build()
            );
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * (개발자용) 전체 메시지 목록
     * 삭제된 메시지도 포함된다
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat")
    public ResponseEntity<List<Chat>> listAllPersonalChat() {
        return new ResponseEntity<>(chatService.listAllPersonalChat(), HttpStatus.OK);
    }

    /**
     * 자신이 전송한 메시지의 목록
     * @param session 세션
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/sent")
    public ResponseEntity<List<Chat>> listSent(HttpSession session) {
        String sessionUserId = getSessionUserId(session);
        if (sessionUserId == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(chatService.listPersonalChatBySender(sessionUserId), HttpStatus.OK);
    }

    /**
     * 자신이 수신한 모든 메시지 목록
     * @param session 세션
     * @return 메시지 객체 리스트
     */
    @GetMapping("/api/v1/chat/received")
    public ResponseEntity<List<Chat>> listReceived(HttpSession session) {
        String sessionUserId = getSessionUserId(session);
        if (sessionUserId == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(chatService.listPersonalChatByReceiver(sessionUserId), HttpStatus.OK);
    }

    /**
     * 자신이 전송한 메시지 하나를 삭제
     * @param chatId 메시지 id
     * @param session   세션
     * @return 메시지 객체
     */
    @DeleteMapping("/api/v1/chat/{chatId}")
    public ResponseEntity<Chat> deleteChat(@PathVariable long chatId, HttpSession session) {
        String sessionUserId = getSessionUserId(session);
        if (sessionUserId == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Chat ret;
        try {
            ret = chatService.deletePersonalChat(chatId, sessionUserId);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @Nullable
    private static String getSessionUserId(HttpSession session) {
        return (String) session.getAttribute(MemberController.SESSION_KEY_USER_ID);
    }

}
