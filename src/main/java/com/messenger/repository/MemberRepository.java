package com.messenger.repository;

import com.messenger.domain.Member;

import java.util.*;

public interface MemberRepository {

    public boolean save(Member member);
    public Optional<Member> findById(String id);
    public List<Member> findByName(String name);
    public List<Member> findAll();
    public boolean updateMember(Member member);
    public Optional<Member> findByIdAndPw(String id, String password);
}