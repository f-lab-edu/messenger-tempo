package com.messenger.domain;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;


@Value
public class Chat {

    long id;
    String message_from;
    String message_to;
    String message;
    short unread_count;
    Timestamp created_at;
    Boolean deleted;

    @Builder
    private Chat(long id, String message_from, String message_to, String message, short unread_count, Timestamp created_at, Boolean deleted) {
        this.id = id;
        this.message_from = message_from;
        this.message_to = message_to;
        this.message = message;
        this.unread_count = unread_count;
        this.created_at = created_at;
        this.deleted = deleted;
    }
}
