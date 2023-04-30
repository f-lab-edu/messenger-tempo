package com.messenger.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Schema(description = "1:1 메시지 전송 RequestDTO")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendGroupChatRequest {

    @Schema(description = "채팅방 id", defaultValue = "roomId", requiredMode = Schema.RequiredMode.REQUIRED)
    private long roomId;

    @NotBlank
    @Schema(description = "메시지 내용", defaultValue = "messageContent")
    private String content;
}
