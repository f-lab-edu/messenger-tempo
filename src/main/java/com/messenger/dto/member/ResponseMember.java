package com.messenger.dto.member;

import com.messenger.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "회원 ResponseDTO")
@Getter
@ToString
public class ResponseMember {

    @Schema(description = "id", defaultValue = "memberId")
    private final String id;

    @Schema(description = "이름", defaultValue = "memberName")
    private final String name;

    @Schema(description = "상태 메시지", defaultValue = "memberStatusMessage")
    private final String statusMessage;

    private ResponseMember(String id, String name, String statusMessage) {
        this.id = id;
        this.name = name;
        this.statusMessage = statusMessage;
    }

    public static ResponseMember of(@NonNull Member member) {
        return new ResponseMember(
                member.getId(),
                member.getName(),
                member.getStatusMessage());
    }

    public static List<ResponseMember> newList(@NonNull List<Member> members) {
        return members.stream().map(ResponseMember::of).collect(Collectors.toList());
    }
}
