package com.messenger.service;

import com.messenger.domain.Member;
import com.messenger.domain.TokenInfo;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.jwt.JwtSecurityConfig;
import com.messenger.jwt.TokenProvider;
import com.messenger.repository.MemberRepository;
import com.messenger.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = tokenProvider;
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

    public Member findById(String id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
        }
        return member.get();
    }

    public List<Member> findByName(String name) {
        List<Member> members = memberRepository.findByName(name);
        if (members.isEmpty()) {
            throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
        }
        return members;
    }

    public Member updateInfo(Member paramMember) throws MyException {
        Member findMember = findById(paramMember.getId());

        if (paramMember.getPassword() != null) {
            findMember.updatePassword(paramMember.getPassword());
        }
        if (paramMember.getName() != null) {
            findMember.updateName(paramMember.getName());
        }
        if (paramMember.getStatusMessage() != null) {
            findMember.updateStatusMessage(paramMember.getStatusMessage());
        }

        Member ret;
        try {
            ret = memberRepository.updateMember(findMember);
        } catch (MyException e) {
            if(!e.errorCode.equals(ErrorCode.NOT_MODIFIED)) {
                throw new MyException(ErrorCode.FAIL_UPDATE_MEMBER);
            }
            throw e;
        }

        return ret;
    }

    public Pair<Member, HttpHeaders> login(String id, String password) throws MyException {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        log.debug("authenticationToken = {}", authenticationToken);
        TokenInfo tokenInfo;
        try {
            // credential 인증하려고 시도하고, 성공하면 Authentication 객체를 반환
            // authenticate()가 실행될때 loadUserByUsername()이 실행된다
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.debug("authenticate = {}", authentication);
            // Authentication 객체를 SecurityContext 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            tokenInfo = tokenProvider.createToken(authentication);
            log.debug("tokenInfo = {}", tokenInfo);
        } catch (DisabledException | LockedException e) {
            // 계정이 disable 이거나 locked 인 경우
            log.debug(e.getMessage());
            throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
        } catch (AuthenticationException | IllegalArgumentException e) {
            // 인증 실패 또는 PasswordEncoder가 지원하지 않는 방식으로 DB에 password가 저장된 경우
            log.debug(e.getMessage());
            throw new MyException(ErrorCode.NOT_MATCH_PASSWORD);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Member findMember = memberRepository.findById(id).orElseThrow(() -> new MyException(ErrorCode.NOT_FOUND_MEMBER));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtSecurityConfig.AUTHORIZATION_HEADER, JwtSecurityConfig.TOKEN_PREFIX + tokenInfo.getAccessToken());

        return new Pair<>(findMember, httpHeaders);
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return findById(id);
    }
}