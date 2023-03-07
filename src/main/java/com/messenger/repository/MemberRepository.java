package com.messenger.repository;

import com.messenger.domain.Member;

import java.util.*;

public interface MemberRepository {


    Member save(Member member);
    List<Member> findAll();
    Optional<Member> findById(String id);
    List<Member> findByName(String name);
    Optional<Member> findByIdAndPw(String id, String password);
    Member updateMember(Member paramMember);
}