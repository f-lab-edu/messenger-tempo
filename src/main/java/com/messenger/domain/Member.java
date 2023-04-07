package com.messenger.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;

@Value
public class Member implements UserDetails {

    String id;
    String password;
    String name;
    String statusMessage;  // 상태 메시지
    MemberRole role;

    @Builder
    private Member(@NonNull String id, @NonNull String password, String name, String statusMessage, MemberRole role) {
        this.id = id;
        this.password = password;
        this.name = (ObjectUtils.isEmpty(name)) ? "undefined": name;
        this.statusMessage = (statusMessage == null) ? "" : statusMessage;
        this.role = (role == null) ? MemberRole.USER : role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
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
