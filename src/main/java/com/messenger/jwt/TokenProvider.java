package com.messenger.jwt;

import com.messenger.domain.Member;
import com.messenger.domain.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String GRANT_TYPE = "Bearer";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    // application.properties 의 설정값을 가져옴
    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValiditySeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValiditySeconds * 1000;
    }
    
    // 문자열을 바이트 배열로 Base64 decode 후, 비밀키로 설정
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        log.debug("keyBytes : {} bytes ({} bits)", keyBytes.length, keyBytes.length * 8);
    }

    // Authentication 객체에 포함되어 있는 권한 정보들을 담은 토큰을 생성하고 jwt.token-validity-in-seconds 값을 이용해 토큰의 만료 시간을 지정
    public TokenInfo createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        return TokenInfo.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .build();
    }

    // getAuthentication 메소드는 토큰에 담겨있는 권한 정보들을 이용해 Authentication 객체를 리턴합니다.
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        log.debug("claims = {}", claims);
        Member principal = Member.builder()
                .id(claims.getSubject())
                .password("")
                .build();

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // validateToken 메소드는 토큰을 검증하는 역할을 수행합니다.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
