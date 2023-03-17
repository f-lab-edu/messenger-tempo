package com.messenger.repository;

import com.messenger.domain.Chat;

import java.util.List;
import java.util.Optional;

public interface PersonalChatRepository {

     Chat save(Chat chat);
     void deleteOne(long chatId, String userId);
     Optional<Chat> findById(long chatId);
     List<Chat> findAll(Integer prevId, Integer size);
     List<Chat> findBySender(String senderUserId, Integer prevId, Integer size);
     List<Chat> findByReceiver(String receiverUserId, Integer prevId, Integer size);
     List<Chat> findByGroup(String userId, String oppositeUserId, Integer prevId, Integer size);
     Optional<Chat> findLastReceivedByGroup(String userId, String oppositeUserId);
     Optional<Chat> markReadById(long chatId);
}
