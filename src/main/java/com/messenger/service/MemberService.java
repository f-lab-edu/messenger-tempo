package com.messenger.service;

import com.messenger.domain.Member;
import com.messenger.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean memberSignup(Member member) {
        boolean result = validateDuplicateMember(member);
        if (!result) {
            return false;
        }
        return memberRepository.save(member);
    }

    public List<Member> memberList() {
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
    public boolean updateMemberName(String id, String name) {
        return memberRepository.updateDisplayName(id, name);
    }

    public boolean updateMemberPassword(String id, String password) {
        return memberRepository.updatePassword(id, password);
    }

    public boolean updateMemberContent(String id, String content) {
        return memberRepository.updateContent(id, content);
    }
}