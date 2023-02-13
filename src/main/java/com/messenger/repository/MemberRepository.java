package com.messenger.repository;

import com.messenger.domain.Member;

import java.util.*;

public interface MemberRepository {

    public boolean save(Member member);
    public Optional<Member> findById(String id);
    public List<Member> findAll();
    public boolean updatePassword(String id, String password);
    public boolean updateDisplayName(String id, String name);
}
