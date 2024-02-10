package com.messenger.dto.chat;

import com.messenger.domain.GroupChat;
import com.messenger.dto.pagination.Pageable;
import com.messenger.util.DateTimeConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "그룹 채팅 ResponseDTO")
@Getter
@ToString
public class GroupChatResponse implements Pageable {

    private final long id;
    private final String senderUserId;
    private final Long roomId;
    private final String content;
    private final String createdAt;

    public GroupChatResponse(GroupChat groupChat) {
        this.id = groupChat.getId();
        this.senderUserId = groupChat.getSenderUserId();
        this.roomId = groupChat.getRoomId();
        this.content = groupChat.getContent();
        this.createdAt = DateTimeConvertor.convertTimestamp2String(groupChat.getCreatedAt());
    }
}
