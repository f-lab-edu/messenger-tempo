package com.messenger.jwt;

import com.messenger.domain.Member;
import com.messenger.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * JWT를 이용한 인증
 */
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    public JwtAuthorizationFilter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("JwtAuthorizationFilter doFilterInternal(...)");
        String token = null;
        // cookie에서 JWT token을 가져온다
        token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtProperties.COOKIE_NAME))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        log.debug("token={}", token);

        if (token != null) {
            try {
                // authentication을 만들어서 SecurityContext에 넣어준다
                Authentication authentication = getUsernamePasswordAuthenticationToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 실패하는 경우에는 쿠키를 초기화
                Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthenticationToken(String token) {
        log.debug("getUsernamePasswordAuthenticationToken() token={}", token);
        // 토큰으로 userId를 찾아온다
        String userId = JwtUtils.getUserId(token);
        if (userId != null) {
            // userId로 Member를 찾아온다
            Member member = memberRepository.findById(userId).orElse(null);
            if (member == null) return null;
            return new UsernamePasswordAuthenticationToken(
                    member, // principal
                    null,
                    null);
        }
        return null; // 유저가 없으면 NULL
    }
}
