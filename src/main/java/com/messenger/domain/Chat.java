package com.messenger.domain;

import com.messenger.dto.pagination.Pageable;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;


@Value
public class Chat implements Pageable {

    long id;
    String senderUserId;
    String receiverUserId;
    String groupId;
    String content;
    Timestamp readAt;
    Timestamp createdAt;

    @Builder
    private Chat(long id, String senderUserId, String receiverUserId, String groupId, String content, Timestamp readAt, Timestamp createdAt) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.groupId = groupId;
        this.content = content;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }
}
