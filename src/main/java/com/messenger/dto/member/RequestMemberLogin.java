package com.messenger.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Schema(description = "회원 가입 RequestDTO")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMemberLogin {

    @NotBlank
    @Schema(description = "id", defaultValue = "memberId")
    private String id;

    @NotBlank
    @Schema(description = "비밀번호", defaultValue = "password")
    private String password;
}
