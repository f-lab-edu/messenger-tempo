package com.messenger.service;

import com.messenger.domain.Member;
import com.messenger.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean signupMember(Member member) {
        boolean result = validateDuplicateMember(member);
        if (!result) {
            return false;
        }
        return memberRepository.save(member);
    }

    public List<Member> listMember() {
        return memberRepository.findAll();
    }

    private boolean validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findById(member.getId());
        return result.isEmpty();
    }

    public Optional<Member> findMemberById(String id) {
        return memberRepository.findById(id);
    }

    public List<Member> findMemberByName(String name) {
        return memberRepository.findByName(name);
    }

    public boolean updateMemberInfo(Member paramMember) {
        Member findMember = findMemberById(paramMember.getId()).orElseThrow();

        if (isEmpty(paramMember.getPassword())) {
            paramMember.setPassword(findMember.getPassword());
        }
        if (isEmpty(paramMember.getName())) {
            paramMember.setName(findMember.getName());
        }
        if (isEmpty(paramMember.getStatusMessage())) {
            paramMember.setStatusMessage(findMember.getStatusMessage());
        }
        return memberRepository.updateMember(paramMember);
    }

    // 문자열이 null 또는 빈 문자열인지 여부
    private static boolean isEmpty(String param) {
        return param == null || param.isEmpty();
    }

    public Optional<Member> loginMember(String id, String password) {
        return memberRepository.findByIdPw(id, password);
    }
}