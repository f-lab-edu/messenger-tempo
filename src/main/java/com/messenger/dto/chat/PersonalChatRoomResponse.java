package com.messenger.dto.chat;

import com.messenger.util.Pair;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "1:1 채팅방 ResponseDTO")
@Getter
@ToString
public class PersonalChatRoomResponse {

    @Schema(description = "상대방 유저 id", defaultValue = "oppositeUserId")
    private final String oppositeUserId;

    @Schema(description = "마지막 채팅 id", defaultValue = "1")
    private final Long lastChatId;

    public PersonalChatRoomResponse(String oppositeUserId, Long lastChatId) {
        this.oppositeUserId = oppositeUserId;
        this.lastChatId = lastChatId;
    }

    public static PersonalChatRoomResponse of(Pair<String, Long> pair) {
        return new PersonalChatRoomResponse(pair.getFirst(), pair.getSecond());
    }
}
