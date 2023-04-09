package com.messenger.domain;

import lombok.*;

@Getter
@ToString
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

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
