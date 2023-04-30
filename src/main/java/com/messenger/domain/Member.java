package com.messenger.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

@Getter
@ToString
public class Member implements UserDetails {

    private final String id;
    private String password;
    private String name;
    private String statusMessage;  // 상태 메시지
    private final MemberRole role;  // 하나의 role만 가진다

    @Builder
    private Member(@NonNull String id, @NonNull String password, String name, String statusMessage, MemberRole role) {
        this.id = id;
        this.password = password;
        this.name = (StringUtils.hasText(name)) ? name : "undefined";
        this.statusMessage = (StringUtils.hasText(statusMessage)) ? statusMessage : "";
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
    public String getPassword() {
        return password;
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
