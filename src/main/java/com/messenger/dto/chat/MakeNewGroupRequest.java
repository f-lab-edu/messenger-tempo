package com.messenger.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "그룹 채팅방 생성 RequestDTO")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MakeNewGroupRequest {

    @Schema(description = "", defaultValue = "")
    private List<String> memberList;
}
