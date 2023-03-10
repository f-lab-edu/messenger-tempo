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

    public Chat sendPersonalChat(Chat chat) {
        return personalChatRepository.save(chat);
    }

    public Chat deletePersonalChat(long chatId, String userId) {
        return personalChatRepository.deleteOne(chatId, userId);
    }

    public List<Chat> listAllPersonalChat() {
        return personalChatRepository.findAll();
    }

    public List<Chat> listPersonalChatBySender(String senderUserId) {
        return personalChatRepository.findBySender(senderUserId);
    }

    public List<Chat> listPersonalChatByReceiver(String receiverUserId) {
        return personalChatRepository.findByReceiver(receiverUserId);
    }
}
