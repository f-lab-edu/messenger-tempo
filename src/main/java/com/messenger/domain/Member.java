package com.messenger.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Member {

    private final String id;
    private String password;
    private String name = "undefined";
    private String statusMessage;  // 상태 메시지

    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public Member(String id, String password, String name) {
        this.id = id;
        this.password = password;
        if (name != null && !name.equals("")) {
            this.name = name;
        }
    }

    public Member(String id, String password, String name, String statusMessage) {
        this.id = id;
        this.password = password;
        if (name != null && !name.equals("")) {
            this.name = name;
        }
        if (statusMessage != null && !statusMessage.equals("")) {
            this.statusMessage = statusMessage;
        }
    }
}
