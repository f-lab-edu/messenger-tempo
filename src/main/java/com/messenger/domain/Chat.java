package com.messenger.domain;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;


@Value
public class Chat {

    long id;
    String senderUserId;
    String receiverUserId;
    String content;
    short unread_count;
    Timestamp created_at;
    Boolean deleted;

    @Builder
    private Chat(long id, String senderUserId, String receiverUserId, String content, short unread_count, Timestamp created_at, Boolean deleted) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.content = content;
        this.unread_count = unread_count;
        this.created_at = created_at;
        this.deleted = deleted;
    }
}
