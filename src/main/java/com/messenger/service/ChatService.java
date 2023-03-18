package com.messenger.service;

import com.messenger.domain.Chat;
import com.messenger.repository.PersonalChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final PersonalChatRepository personalChatRepository;

    public ChatService(PersonalChatRepository personalChatRepository) {
        this.personalChatRepository = personalChatRepository;
    }

    public Chat sendPersonalChat(Chat chat) {
        return personalChatRepository.save(chat);
    }

    public void deletePersonalChat(long chatId, String userId) {
        personalChatRepository.deleteOne(chatId, userId);
    }

    public List<Chat> listAllPersonalChat(Integer prevId, Integer size) {
        return personalChatRepository.findAll(prevId, size);
    }

    public List<Chat> listPersonalChatBySender(String senderUserId, Integer prevId, Integer size) {
        return personalChatRepository.findBySender(senderUserId, prevId, size);
    }

    public List<Chat> listPersonalChatByReceiver(String receiverUserId, Integer prevId, Integer size) {
        return personalChatRepository.findByReceiver(receiverUserId, prevId, size);
    }

    public List<Chat> listPersonalChatByGroup(String userId, String oppositeUserId, Integer prevId, Integer size) {
        return personalChatRepository.findByGroup(userId, oppositeUserId, prevId, size);
    }

    public Optional<Chat> markPersonalChatAsReadByGroup(String userId, String oppositeUserId) {
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
}
