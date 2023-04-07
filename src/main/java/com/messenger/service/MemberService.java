package com.messenger.service;

import com.messenger.domain.Member;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.repository.MemberRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public Member signup(Member member) throws MyException {
        Member result;
        try {
            Member modifiedMember = Member.builder()
                    .id(member.getId())
                    .password(passwordEncoder.encode(member.getPassword()))
                    .name(member.getName())
                    .statusMessage(member.getStatusMessage())
                    .build();
            result = memberRepository.save(modifiedMember);
        } catch (MyException e) {
            throw new MyException(ErrorCode.FAIL_SIGNUP);
        }
        return result;
    }

    public List<Member> listAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public List<Member> findByName(String name) {
        return memberRepository.findByName(name);
    }

    public Member updateInfo(Member paramMember) throws MyException {
        Member findMember = findById(paramMember.getId()).orElseThrow(() -> new MyException(ErrorCode.NOT_FOUND_MEMBER));

        String memberId = paramMember.getId();
        String modifiedPassword = paramMember.getPassword();
        String modifiedName = paramMember.getName();
        String modifiedStatusMessage = paramMember.getStatusMessage();

        if (paramMember.getPassword() == null) {
            modifiedPassword = findMember.getPassword();
        }
        if (paramMember.getName() == null) {
            modifiedName = findMember.getName();
        }
        if (paramMember.getStatusMessage() == null) {
            modifiedStatusMessage = findMember.getStatusMessage();
        }
        Member ret;
        try {
            ret = memberRepository.updateMember(
                    Member.builder()
                            .id(memberId)
                            .password(modifiedPassword)
                            .name(modifiedName)
                            .statusMessage(modifiedStatusMessage)
                            .build());
        } catch (MyException e) {
            if(!e.errorCode.equals(ErrorCode.NOT_MODIFIED)) {
                throw new MyException(ErrorCode.FAIL_UPDATE_MEMBER);
            }
            throw e;
        }

        return ret;
    }

    public Member login(String id, String password) throws MyException {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        return memberRepository.findByIdAndPw(id, password).orElseThrow(() -> new MyException(ErrorCode.NOT_MATCH_PASSWORD));
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return findById(id).orElseThrow(() -> new MyException(ErrorCode.NOT_FOUND_MEMBER));
    }
}