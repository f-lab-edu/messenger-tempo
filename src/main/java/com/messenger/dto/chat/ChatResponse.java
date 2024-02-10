package com.messenger.dto.chat;

import com.messenger.domain.Chat;
import com.messenger.dto.pagination.Pageable;
import com.messenger.util.DateTimeConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "1:1 채팅 ResponseDTO")
@Getter
@ToString
public class ChatResponse implements Pageable {

    private final long id;
    private final String senderUserId;
    private final String receiverUserId;
    private final String groupId;
    private final String content;
    private final String readAt;
    private final String createdAt;

    public ChatResponse(Chat chat) {
        this.id = chat.getId();
        this.senderUserId = chat.getSenderUserId();
        this.receiverUserId = chat.getReceiverUserId();
        this.groupId = chat.getGroupId();
        this.content = chat.getContent();
        this.readAt = DateTimeConvertor.convertTimestamp2String(chat.getReadAt());
        this.createdAt = DateTimeConvertor.convertTimestamp2String(chat.getCreatedAt());
    }
}
