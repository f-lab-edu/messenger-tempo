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
public class MemberResponse {

    @Schema(description = "id", defaultValue = "memberId")
    private final String id;

    @Schema(description = "이름", defaultValue = "memberName")
    private final String name;

    @Schema(description = "상태 메시지", defaultValue = "memberStatusMessage")
    private final String statusMessage;

    private MemberResponse(String id, String name, String statusMessage) {
        this.id = id;
        this.name = name;
        this.statusMessage = statusMessage;
    }

    public static MemberResponse of(@NonNull Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getStatusMessage());
    }

    public static List<MemberResponse> of(@NonNull List<Member> members) {
        return members.stream().map(MemberResponse::of).collect(Collectors.toList());
    }
}
