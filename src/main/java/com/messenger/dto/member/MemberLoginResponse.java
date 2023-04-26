package com.messenger.dto.member;

import com.messenger.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "회원 로그인 ResponseDTO")
@Getter
@Setter
@ToString
public class MemberLoginResponse {

    @Schema(description = "id", defaultValue = "memberId")
    private final String id;

    @Schema(description = "이름", defaultValue = "memberName")
    private final String name;

    @Schema(description = "상태 메시지", defaultValue = "memberStatusMessage")
    private final String statusMessage;

    @Schema(description = "jwt 토큰", defaultValue = "token")
    private String token;

    private MemberLoginResponse(String id, String name, String statusMessage) {
        this.id = id;
        this.name = name;
        this.statusMessage = statusMessage;
    }

    public static MemberLoginResponse of(@NonNull Member member) {
        return new MemberLoginResponse(
                member.getId(),
                member.getName(),
                member.getStatusMessage());
    }
}
