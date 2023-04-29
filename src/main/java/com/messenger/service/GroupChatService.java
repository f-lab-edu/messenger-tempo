package com.messenger.service;

import com.messenger.domain.GroupChat;
import com.messenger.dto.chat.MakeNewGroupRequest;
import com.messenger.dto.chat.SendGroupChatRequest;
import com.messenger.dto.chat.GroupChatRoomResponse;
import com.messenger.dto.pagination.PaginationRequest;
import com.messenger.dto.pagination.PaginationResponse;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.repository.GroupChatRepository;
import com.messenger.util.Pair;
import com.messenger.util.SpringSecurityUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;

    public GroupChatService(GroupChatRepository groupChatRepository) {
        this.groupChatRepository = groupChatRepository;
    }

    public Optional<GroupChat> getGroupChat(@NonNull long chatId) {
        return groupChatRepository.findById(chatId);
    }

    public GroupChat sendGroupChat(SendGroupChatRequest request) {

        Long roomId = request.getRoomId();
        String content = request.getContent();

        String userId = SpringSecurityUtil.getAuthenticationName();

        GroupChat chat = GroupChat.builder()
                .senderUserId(userId)
                .roomId(roomId)
                .content(content)
                .build();
        GroupChat result;
        try {
            result = groupChatRepository.save(chat);
        } catch(Exception e) {
            throw new MyException(ErrorCode.FAIL_SAVE_CHAT);
        }
        return result;
    }

    public void deletePersonalChat(@NonNull long chatId) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        try {
            groupChatRepository.deleteOne(chatId, userId);
        } catch (Exception e) {
            throw new MyException(ErrorCode.FAIL_DELETE_CHAT);
        }
    }

    public List<GroupChat> listPersonalChatByReceiver(PaginationRequest request) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        return groupChatRepository.findByReceiver(userId, request.getNextId(), request.getSize());
    }

    public List<GroupChat> listChatByGroup(@NonNull Long roomId, PaginationRequest request) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        // 유저가 방에 속하지 않은 경우 빈 리스트 반환
        if (!groupChatRepository.belongToRoom(roomId, userId)) {
            return Collections.emptyList();
        }

        return groupChatRepository.findByGroup(userId, roomId, request.getNextId(), request.getSize());
    }

    public List<GroupChat> listChatByGroup(@NonNull Long roomId, Integer size) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        // 유저가 방에 속하지 않은 경우 빈 리스트 반환
        if (!groupChatRepository.belongToRoom(roomId, userId)) {
            return Collections.emptyList();
        }

        return groupChatRepository.findByGroup(userId, roomId, null, size);
    }

    public Optional<GroupChat> markPersonalChatAsReadByGroup(@NonNull String userId, @NonNull Long roomId) {
        // 1:1 채팅 그룹 안에서 자신이 받은 마지막 메시지를 찾는다
        Optional<GroupChat> foundChat = groupChatRepository.findLastReceivedByGroup(userId, roomId);

        // 자신이 받은 마지막 메시지가 없는 경우
        if (foundChat.isEmpty()) {
            return foundChat;
        }

        long chatId = foundChat.get().getId();

        // 마지막 메시지가 이미 읽음 표시가 된 경우
        Map<String, Timestamp> readStatus = groupChatRepository.getReadStatusById(chatId);
        if (readStatus.get(userId) != null) {
            return foundChat;
        }

        // 마지막 메시지를 읽음 표시
        return groupChatRepository.markReadById(chatId, userId);
    }

    public PaginationResponse<GroupChat> enterGroupChat(@NonNull Long roomId, Integer size) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        // 해당 그룹의 메시지 목록을 가져옴
        List<GroupChat> chatList = listChatByGroup(roomId, size);
        PaginationResponse<GroupChat> result = PaginationResponse.of(chatList);

        // 가장 최근 수신한 메시지를 읽음 표시
        Optional<GroupChat> markedChat = markPersonalChatAsReadByGroup(userId, roomId);

        result.setLatestReceivedChat(markedChat.orElse(null));
        return result;
    }

    /**
     * 테스트용
     */
    public List<GroupChatRoomResponse> listGroupByUser(@NonNull String userId) {

        List<Pair<Long, Long>> list = groupChatRepository.listGroupByUser(userId);
        return list.stream().map(GroupChatRoomResponse::of).collect(Collectors.toList());
    }

    public List<GroupChatRoomResponse> listGroupByUser() {

        String userId = SpringSecurityUtil.getAuthenticationName();

        return listGroupByUser(userId);
    }

    public List<String> makeNewGroup(MakeNewGroupRequest request) {

        String userId = SpringSecurityUtil.getAuthenticationName();

        List<String> memberList = request.getMemberList();
        log.debug("memberList = {}", memberList);
        memberList.add(userId);

        return groupChatRepository.makeNewGroup(memberList);
    }
}
