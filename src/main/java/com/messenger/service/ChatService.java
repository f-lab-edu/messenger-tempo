package com.messenger.service;

import com.messenger.domain.Chat;
import com.messenger.repository.PersonalChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final PersonalChatRepository personalChatRepository;

    public ChatService(PersonalChatRepository personalChatRepository) {
        this.personalChatRepository = personalChatRepository;
    }

    public Chat sendChat1on1(Chat chat) {
        return personalChatRepository.save(chat);
    }

    public Chat deleteChat1on1(long messageId, String userId) {
        return personalChatRepository.deleteOne(messageId, userId);
    }

    public List<Chat> listAllChat1on1() {
        return personalChatRepository.findAll();
    }

    public List<Chat> listChat1on1ByFrom(String senderUserId) {
        return personalChatRepository.findBySender(senderUserId);
    }

    public List<Chat> listChat1on1ByTo(String receiverUserId) {
        return personalChatRepository.findByReceiver(receiverUserId);
    }
}
