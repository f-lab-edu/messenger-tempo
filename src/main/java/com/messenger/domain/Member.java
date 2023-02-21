package com.messenger.domain;

import lombok.*;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class Member {

    String id;
    String password;
    @Builder.Default String name = "undefined";
    @Builder.Default String statusMessage = "";  // 상태 메시지

    public static MemberBuilder builder(String id, String password) {
        return hiddenBuilder().id(id).password(password);
    }

}
