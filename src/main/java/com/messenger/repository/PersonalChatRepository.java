package com.messenger.repository;

import com.messenger.domain.Chat;

import java.util.List;
import java.util.Optional;

public interface PersonalChatRepository {

     Chat save(Chat chat);
     Chat deleteOne(long chatId, String userId);
     Optional<Chat> findById(long chatId);
     List<Chat> findAll();
     List<Chat> findBySender(String senderUserId);
     List<Chat> findByReceiver(String receiverUserId);
}
