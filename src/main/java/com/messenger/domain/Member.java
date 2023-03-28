package com.messenger.domain;

import lombok.Value;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Value
public class Member implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
