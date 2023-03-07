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

    public Chat sendChat1on1(Chat chat) {
        return personalChatRepository.save(chat);
    }

    public Chat deleteChat1on1(long messageId, String userId) {
        Optional<Chat> ret = personalChatRepository.deleteOne(messageId, userId);
        if (ret.isEmpty()) {
            throw new NullPointerException("cannot find chat by id");
        }
        return ret.get();
    }

    public List<Chat> listAllChat1on1() {
        return personalChatRepository.findAll();
    }

    public List<Chat> listChat1on1ByFrom(String message_from) {
        return personalChatRepository.findByFrom(message_from);
    }

    public List<Chat> listChat1on1ByTo(String message_to) {
        return personalChatRepository.findByTo(message_to);
    }
}
