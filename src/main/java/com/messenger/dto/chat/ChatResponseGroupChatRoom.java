package com.messenger.dto.chat;

import com.messenger.util.Pair;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "그룹 채팅방 ResponseDTO")
@Getter
@ToString
public class ChatResponseGroupChatRoom {

    @Schema(description = "그룹 채팅방 id", defaultValue = "0")
    private final Long roomId;

    @Schema(description = "마지막 채팅 id", defaultValue = "0")
    private final Long lastChatId;

    public ChatResponseGroupChatRoom(Long roomId, Long lastChatId) {
        this.roomId = roomId;
        this.lastChatId = lastChatId;
    }

    public static ChatResponseGroupChatRoom of(Pair<Long, Long> pair) {
        return new ChatResponseGroupChatRoom(pair.getFirst(), pair.getSecond());
    }
}
