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

    public boolean signup(Member member) {
        boolean result = validateDuplicateMember(member);
        if (!result) {
            return false;
        }
        return memberRepository.save(member);
    }

    public List<Member> listAll() {
        return memberRepository.findAll();
    }

    private boolean validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findById(member.getId());
        return result.isEmpty();
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public List<Member> findByName(String name) {
        return memberRepository.findByName(name);
    }

    public boolean updateInfo(Member paramMember) {
        Member findMember = findById(paramMember.getId()).orElseThrow();

        String memberId = paramMember.getId();
        String modifiedPassword = paramMember.getPassword();
        String modifiedName = paramMember.getName();
        String modifiedStatusMessage = paramMember.getStatusMessage();

        if (paramMember.getPassword().equals("")) {
            modifiedPassword = findMember.getPassword();
        }
        if (paramMember.getName().equals("")) {
            modifiedName = findMember.getName();
        }
        if (paramMember.getStatusMessage().equals("")) {
            modifiedStatusMessage = findMember.getStatusMessage();
        }
        return memberRepository.updateMember(
                new Member(memberId, modifiedPassword, modifiedName, modifiedStatusMessage)
        );
    }

    public Optional<Member> login(String id, String password) {
        return memberRepository.findByIdAndPw(id, password);
    }
}