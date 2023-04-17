package com.messenger.service;

import com.messenger.domain.Chat;
import com.messenger.dto.PaginationWrapper;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.repository.PersonalChatRepository;
import com.messenger.util.Pair;
import com.messenger.util.SpringSecurityUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final PersonalChatRepository personalChatRepository;

    public ChatService(PersonalChatRepository personalChatRepository) {
        this.personalChatRepository = personalChatRepository;
    }

    public Optional<Chat> getPersonalChat(@NonNull long chatId) {
        return personalChatRepository.findById(chatId);
    }

    public Chat sendPersonalChat(@NonNull String receiverUserId, @NonNull String content) {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }

        Chat chat = Chat.builder()
                .senderUserId(userId)
                .receiverUserId(receiverUserId)
                .content(content)
                .build();
        Chat result;
        try {
            result = personalChatRepository.save(chat);
        } catch(Exception e) {
            throw new MyException(ErrorCode.FAIL_SAVE_CHAT);
        }
        return result;
    }

    public void deletePersonalChat(@NonNull long chatId) {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }

        try {
            personalChatRepository.deleteOne(chatId, userId);
        } catch (Exception e) {
            throw new MyException(ErrorCode.FAIL_DELETE_CHAT);
        }
    }

    public List<Chat> listAllPersonalChat(Integer prevId, Integer size) {
        return personalChatRepository.findAll(prevId, size);
    }

    public List<Chat> listPersonalChatBySender(Integer prevId, Integer size) {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }
        return personalChatRepository.findBySender(userId, prevId, size);
    }

    public List<Chat> listPersonalChatByReceiver(Integer prevId, Integer size) {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }
        return personalChatRepository.findByReceiver(userId, prevId, size);
    }

    public List<Chat> listPersonalChatByGroup(@NonNull String oppositeUserId, Integer prevId, Integer size) {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }
        return personalChatRepository.findByGroup(userId, oppositeUserId, prevId, size);
    }

    public Optional<Chat> markPersonalChatAsReadByGroup(@NonNull String userId, @NonNull String oppositeUserId) {
        // 1:1 채팅 그룹 안에서 자신이 받은 마지막 메시지를 찾는다
        Optional<Chat> foundChat = personalChatRepository.findLastReceivedByGroup(userId, oppositeUserId);

        // 자신이 받은 마지막 메시지가 없는 경우
        if (foundChat.isEmpty()) {
            return foundChat;
        }

        // 마지막 메시지가 이미 읽음 표시가 된 경우
        if (foundChat.get().getRead_at() != null) {
            return foundChat;
        }

        // 마지막 메시지를 읽음 표시
        long chatId = foundChat.get().getId();
        return personalChatRepository.markReadById(chatId);
    }

    public PaginationWrapper<Chat> enterPersonalChatGroup(@NonNull String oppositeUserId, Integer size) {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }

        // 해당 그룹의 메시지 목록을 가져옴
        List<Chat> chatList = listPersonalChatByGroup(oppositeUserId, null, size);
        PaginationWrapper<Chat> result = new PaginationWrapper<>(chatList);

        // 가장 최근 수신한 메시지를 읽음 표시
        Optional<Chat> markedChat = markPersonalChatAsReadByGroup(userId, oppositeUserId);

        result.setLatestReceivedChat(markedChat.orElse(null));
        return result;
    }

    /**
     * 테스트용
     */
    public List<Pair<String, Long>> listGroupByUser(@NonNull String userId) {
        return personalChatRepository.listGroupByUser(userId);
    }

    public List<Pair<String, Long>> listGroupByUser() {
        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }

        return personalChatRepository.listGroupByUser(userId);
    }
}
