package com.messenger.service;

import com.messenger.domain.Chat;
import com.messenger.repository.Chat1on1Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final Chat1on1Repository chat1on1Repository;

    public ChatService(Chat1on1Repository chat1on1Repository) {
        this.chat1on1Repository = chat1on1Repository;
    }

    public Chat sendChat1on1(Chat chat) {
        return chat1on1Repository.save(chat);
    }

    public Chat deleteChat1on1(long messageId, String userId) {
        Optional<Chat> ret = chat1on1Repository.deleteOne(messageId, userId);
        if (ret.isEmpty()) {
            throw new NullPointerException("cannot find chat by id");
        }
        return ret.get();
    }

    public List<Chat> listAllChat1on1() {
        return chat1on1Repository.findAll();
    }

    public List<Chat> listChat1on1ByFrom(String message_from) {
        return chat1on1Repository.findByFrom(message_from);
    }

    public List<Chat> listChat1on1ByTo(String message_to) {
        return chat1on1Repository.findByTo(message_to);
    }
}
