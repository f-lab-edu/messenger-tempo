package com.messenger.repository;

import com.messenger.domain.Member;

import java.util.*;

public interface MemberRepository {

    public boolean save(Member member);
    public Optional<Member> findById(String id);
    public List<Member> findByName(String name);
    public List<Member> findAll();
    public boolean updatePassword(String id, String password);
    public boolean updateDisplayName(String id, String name);
    public boolean updateContent(String id, String content);
}