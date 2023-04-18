package com.messenger.dto.chat;

import com.messenger.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "1:1 메시지 전송 RequestDTO")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRequest {

    Long chatId;
    String senderUserId;
    String receiverUserId;
    String groupId;
    String content;

    public ChatRequest(Long chatId, String senderUserId, String receiverUserId, String groupId, String content) {
        this.chatId = chatId;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.groupId = groupId;
        this.content = content;
    }

    public Chat toChat() {
        return Chat.builder()
                .id(chatId)
                .senderUserId(senderUserId)
                .receiverUserId(receiverUserId)
                .groupId(groupId)
                .content(content)
                .build();
    }
}
