//package com.messenger.jwt;
//
//import com.messenger.domain.Member;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * JWT를 이용한 로그인 인증
// */
//@Slf4j
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//
//    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
//        super(authenticationManager);
//        this.authenticationManager = authenticationManager;
//    }
//
//    /**
//     * 로그인 인증 시도
//     */
//    @Override
//    public Authentication attemptAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws AuthenticationException {
//        log.info("attemptAuthentication 로그인 인증 시도");
//        // 로그인할 때 id와 password를 가지고 authenticationToken을 생성한다
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                request.getParameter("id"),
//                request.getParameter("password"),
//                new ArrayList<>());
//        return authenticationManager.authenticate(authenticationToken);
//    }
//
//    /**
//     * 인증에 성공했을 때 사용
//     * JWT token을 생성해서 쿠키에 넣는다
//     */
//    @Override
//    protected void successfulAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain chain,
//            Authentication authResult
//    ) throws IOException {
//        log.info("successfulAuthentication()");
//        Member member = (Member) authResult.getPrincipal();
//        String token = JwtUtils.createToken(member);
//
//        // 쿠키 생성
//        Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, token);
//        cookie.setMaxAge(JwtProperties.EXPIRATION_TIME);
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        response.sendRedirect("/");
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            AuthenticationException failed
//    ) throws IOException {
//        log.error("unsuccessfulAuthentication()");
//        // 리다이렉트
//        response.sendRedirect("/login");
//    }
//}
