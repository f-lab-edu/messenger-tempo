package com.messenger.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Schema(description = "그룹 채팅방 생성 RequestDTO")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MakeNewGroupResponse {

    @Schema(description = "", defaultValue = "")
    private List<String> memberList;

    @Schema(description = "", defaultValue = "")
    private Long roomId;

    public MakeNewGroupResponse(List<String> memberList, Long roomId) {
        this.memberList = memberList;
        this.roomId = roomId;
    }

    public static MakeNewGroupResponse of(List<String> memberList, Long roomId) {
        return new MakeNewGroupResponse(memberList, roomId);
    }
}
