package com.messenger.repository;

import com.messenger.domain.Chat;

import java.util.List;
import java.util.Optional;

public interface PersonalChatRepository {

     Chat save(Chat chat);
     Optional<Chat> deleteOne(long messageId, String userId);
     Optional<Chat> findById(long id);
     List<Chat> findAll();
     List<Chat> findByFrom(String message_from);
     List<Chat> findByTo(String message_to);
}
