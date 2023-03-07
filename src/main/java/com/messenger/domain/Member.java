package com.messenger.domain;

import lombok.Value;
import lombok.Builder;
import lombok.NonNull;

@Value
public class Member {

    String id;
    String password;
    String name;
    String statusMessage;  // 상태 메시지

    @Builder
    private Member(@NonNull String id, @NonNull String password, String name, String statusMessage) {
        this.id = id;
        this.password = password;
        this.name = (name == null) ? "undefined": name;
        this.statusMessage = (statusMessage == null) ? "" : statusMessage;
    }
}
