package com.messenger.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Member {

    private String id;
    private String password;
    private String name = "undefined";

    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public Member(String id, String password, String name) {
        this.id = id;
        this.password = password;
        if (!name.equals("")) {
            this.name = name;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
