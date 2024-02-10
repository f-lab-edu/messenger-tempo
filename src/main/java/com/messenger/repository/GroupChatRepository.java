package com.messenger.repository;

import com.messenger.domain.GroupChat;
import com.messenger.dto.chat.MakeNewGroupResponse;
import com.messenger.util.Pair;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GroupChatRepository {

     GroupChat save(GroupChat chat);
     void deleteOne(long chatId, String userId);
     Optional<GroupChat> findById(long chatId);
     List<GroupChat> findByReceiver(String receiverUserId, Integer prevId, Integer size);
     List<GroupChat> findByGroup(String userId, long roomId, Integer prevId, Integer size);
     Optional<GroupChat> findLastReceivedByGroup(String userId, long roomId);
     Map<String, Timestamp> getReadStatusById(long chatId);
     Optional<GroupChat> markReadById(long chatId, String userId);
     List<Pair<Long, Long>> listGroupByUser(String userId);
     boolean belongToRoom(long roomId, String userId);
     MakeNewGroupResponse makeNewGroup(List<String> memberList);
     List<String> listMemberIdByGroup(long roomId);
}
