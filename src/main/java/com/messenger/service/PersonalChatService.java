package com.messenger.service;

import com.messenger.domain.Chat;
import com.messenger.dto.chat.PersonalChatRoomResponse;
import com.messenger.dto.pagination.PaginationRequest;
import com.messenger.dto.pagination.PaginationResponse;
import com.messenger.dto.chat.SendPersonalChatRequest;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.repository.PersonalChatRepository;
import com.messenger.util.Pair;
import com.messenger.util.SpringSecurityUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonalChatService {

    private final PersonalChatRepository personalChatRepository;

    public PersonalChatService(PersonalChatRepository personalChatRepository) {
        this.personalChatRepository = personalChatRepository;
    }

    public Optional<Chat> getPersonalChat(@NonNull long chatId) {
        return personalChatRepository.findById(chatId);
    }

    public Chat sendPersonalChat(SendPersonalChatRequest request) {

        String receiverUserId = request.getReceiverUserId();
        String content = request.getContent();

        String userId = SpringSecurityUtil.getAuthenticationName();

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

        try {
            personalChatRepository.deleteOne(chatId, userId);
        } catch (Exception e) {
            throw new MyException(ErrorCode.FAIL_DELETE_CHAT);
        }
    }

    public List<Chat> listAllPersonalChat(PaginationRequest request) {
        return personalChatRepository.findAll(request.getNextId(), request.getSize());
    }

    public List<Chat> listPersonalChatBySender(PaginationRequest request) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        return personalChatRepository.findBySender(userId, request.getNextId(), request.getSize());
    }

    public List<Chat> listPersonalChatByReceiver(PaginationRequest request) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        return personalChatRepository.findByReceiver(userId, request.getNextId(), request.getSize());
    }

    public List<Chat> listPersonalChatByGroup(@NonNull String oppositeUserId, PaginationRequest request) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        return personalChatRepository.findByGroup(userId, oppositeUserId, request.getNextId(), request.getSize());
    }

    public List<Chat> listPersonalChatByGroup(@NonNull String oppositeUserId, Integer size) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        return personalChatRepository.findByGroup(userId, oppositeUserId, null, size);
    }

    public Optional<Chat> markPersonalChatAsReadByGroup(@NonNull String userId, @NonNull String oppositeUserId) {
        // 1:1 채팅 그룹 안에서 자신이 받은 마지막 메시지를 찾는다
        Optional<Chat> foundChat = personalChatRepository.findLastReceivedByGroup(userId, oppositeUserId);

        // 자신이 받은 마지막 메시지가 없는 경우
        if (foundChat.isEmpty()) {
            return foundChat;
        }

        // 마지막 메시지가 이미 읽음 표시가 된 경우
        if (foundChat.get().getReadAt() != null) {
            return foundChat;
        }

        // 마지막 메시지를 읽음 표시
        long chatId = foundChat.get().getId();
        return personalChatRepository.markReadById(chatId);
    }

    public PaginationResponse<Chat> enterPersonalChatGroup(@NonNull String oppositeUserId, Integer size) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        // 해당 그룹의 메시지 목록을 가져옴
        List<Chat> chatList = listPersonalChatByGroup(oppositeUserId, size);
        PaginationResponse<Chat> result = PaginationResponse.of(chatList);

        // 가장 최근 수신한 메시지를 읽음 표시
        Optional<Chat> markedChat = markPersonalChatAsReadByGroup(userId, oppositeUserId);

        result.setLatestReceivedChat(markedChat.orElse(null));
        return result;
    }

    /**
     * 테스트용
     */
    public List<PersonalChatRoomResponse> listGroupByUser(@NonNull String userId) {

        List<Pair<String, Long>> list = personalChatRepository.listGroupByUser(userId);
        return list.stream().map(PersonalChatRoomResponse::of).collect(Collectors.toList());
    }

    public List<PersonalChatRoomResponse> listGroupByUser() {

        String userId = SpringSecurityUtil.getAuthenticationName();

        List<Pair<String, Long>> list = personalChatRepository.listGroupByUser(userId);
        return list.stream().map(PersonalChatRoomResponse::of).collect(Collectors.toList());
    }
}
