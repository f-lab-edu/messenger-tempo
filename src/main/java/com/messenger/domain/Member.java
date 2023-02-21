package com.messenger.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(builderMethodName = "hiddenBuilder")
@EqualsAndHashCode
@ToString
public class Member {

    private final String id;
    private String password;
    @Builder.Default private String name = "undefined";
    @Builder.Default private String statusMessage = "";  // 상태 메시지

    public static MemberBuilder builder(String id, String password) {
        return hiddenBuilder().id(id).password(password);
    }

}
