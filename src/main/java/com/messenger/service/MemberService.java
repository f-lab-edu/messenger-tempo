package com.messenger.service;

import com.messenger.domain.Member;
import com.messenger.domain.TokenInfo;
import com.messenger.dto.member.MemberRequestLogin;
import com.messenger.dto.member.MemberRequestSignup;
import com.messenger.dto.member.MemberRequestUpdateInfo;
import com.messenger.dto.member.MemberResponseLogin;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.jwt.JwtSecurityConfig;
import com.messenger.jwt.TokenProvider;
import com.messenger.repository.MemberRepository;
import com.messenger.util.Pair;
import com.messenger.util.SpringSecurityUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final Environment env;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, Environment env) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = tokenProvider;
        this.env = env;
    }

    public Member signup(MemberRequestSignup request) {
        Member member = request.toMember();
        member.updatePassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    public List<Member> listAll() {
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
        }
        return members;
    }

    public Member findById(@NonNull String id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
        }
        return member.get();
    }

    public List<Member> findByName(@NonNull String name) {
        List<Member> members = memberRepository.findByName(name);
        if (members.isEmpty()) {
            throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
        }
        return members;
    }

    public Member updateInfo(MemberRequestUpdateInfo request) {

        log.debug("request = {}", request);

        String userId = SpringSecurityUtil.getAuthenticationName();
        if (userId == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }

        Member findMember = findById(userId);
        if (request.getPassword() != null) {
            findMember.updatePassword(request.getPassword());
        }
        if (request.getName() != null) {
            findMember.updateName(request.getName());
        }
        if (request.getStatusMessage() != null) {
            findMember.updateStatusMessage(request.getStatusMessage());
        }

        return memberRepository.updateMember(findMember);
    }

    public Pair<MemberResponseLogin, Cookie> login(MemberRequestLogin request) {

        String id = request.getId();
        String password = request.getPassword();

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
        MemberResponseLogin memberResponse = MemberResponseLogin.of(findMember);
        memberResponse.setToken(tokenInfo.getAccessToken());

        Cookie cookie = new Cookie(JwtSecurityConfig.AUTHORIZATION_COOKIE, tokenInfo.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(env.getProperty("jwt.token-validity-in-seconds", Integer.class));

        return new Pair<>(memberResponse, cookie);
    }

    public Cookie logout() {
        // 쿠키를 삭제
        Cookie cookie = new Cookie(JwtSecurityConfig.AUTHORIZATION_COOKIE, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return findById(id);
    }
}