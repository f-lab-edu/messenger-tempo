package com.messenger.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "회원 정보 변경 RequestDTO")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequestUpdateInfo {

    @Schema(description = "비밀번호")
    String password;

    @Schema(description = "이름")
    String name;
    
    @Schema(description = "상태 메시지")
    String statusMessage;
}
