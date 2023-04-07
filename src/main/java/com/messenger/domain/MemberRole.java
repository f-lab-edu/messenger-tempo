package com.messenger.domain;

import org.springframework.security.core.GrantedAuthority;

public enum MemberRole implements GrantedAuthority {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    MemberRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
