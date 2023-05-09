package com.messenger.dto.chat;

import com.messenger.domain.GroupChat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "그룹 채팅방 ResponseDTO")
@Getter
@ToString
public class GroupChatRoomResponse {

    @Schema(description = "그룹 채팅방 id", defaultValue = "0")
    private final Long roomId;

    @Schema(description = "마지막 채팅 객체")
    private final GroupChatResponse lastChat;

    public GroupChatRoomResponse(Long roomId, GroupChat lastChat) {
        this.roomId = roomId;
        this.lastChat = new GroupChatResponse(lastChat);
    }

    public static GroupChatRoomResponse of(Long roomId, GroupChat lastChat) {
        return new GroupChatRoomResponse(roomId, lastChat);
    }
}
