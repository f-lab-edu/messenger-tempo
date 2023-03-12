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

    public List<Chat> listAllPersonalChat(Integer prevChatId, Integer size) {
        return personalChatRepository.findAll(prevChatId, size);
    }

    public List<Chat> listPersonalChatBySender(String senderUserId, Integer prevChatId, Integer size) {
        return personalChatRepository.findBySender(senderUserId, prevChatId, size);
    }

    public List<Chat> listPersonalChatByReceiver(String receiverUserId, Integer prevChatId, Integer size) {
        return personalChatRepository.findByReceiver(receiverUserId, prevChatId, size);
    }
}
