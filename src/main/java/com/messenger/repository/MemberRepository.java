package com.messenger.repository;

import com.messenger.domain.Member;

import java.util.*;

public interface MemberRepository {

    /**
     * 회원정보를 저장소에 저장
     * @param member 저장할 회원 객체
     * @return 저장 성공 여부
     */
    boolean save(Member member);

    /**
     * 회원 목록
     * @return 회원 객체의 List
     */
    List<Member> findAll();

    /**
     * 저장소에서 id 기반으로 회원 검색
     * @param id 검색할 회원 id
     * @return (nullable)검색된 회원 객체
     */
    Optional<Member> findById(String id);

    /**
     * 저장소에서 이름 기반으로 회원 검색
     * @param name 검색할 회원 이름
     * @return 검색된 회원 객체의 List
     */
    List<Member> findByName(String name);

    /**
     * 저장소에서 id와 비밀번호 기반으로 회원 검색
     * @param id 검색할 회원 id
     * @param password 검색할 회원 비밀번호
     * @return (nullable)검색된 회원 객체
     */
    Optional<Member> findByIdAndPw(String id, String password);

    /**
     * 회원 정보 변경
     * @param paramMember 변경할 회원 정보 객체
     * @return 변경 성공 여부
     */
    boolean updateMember(Member paramMember);
}