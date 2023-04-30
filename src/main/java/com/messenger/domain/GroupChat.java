package com.messenger.domain;

import com.messenger.dto.pagination.Pageable;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;


@Value
public class GroupChat implements Pageable {

    long id;
    String senderUserId;
    Long roomId;
    String content;
    Timestamp createdAt;

    @Builder
    private GroupChat(long id, String senderUserId, Long roomId, String content, Timestamp createdAt) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.roomId = roomId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
