package com.messenger.domain;

import com.messenger.dto.Pageable;
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
    Timestamp read_at;
    Timestamp created_at;

    @Builder
    private Chat(long id, String senderUserId, String receiverUserId, String groupId, String content, Timestamp read_at, Timestamp created_at) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.groupId = groupId;
        this.content = content;
        this.read_at = read_at;
        this.created_at = created_at;
    }
}
