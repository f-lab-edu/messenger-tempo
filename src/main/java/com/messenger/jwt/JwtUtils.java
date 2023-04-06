//package com.messenger.jwt;
//
//import com.messenger.domain.Member;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwsHeader;
//import io.jsonwebtoken.Jwts;
//import lombok.extern.slf4j.Slf4j;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.List;
//
//@Slf4j
//public class JwtUtils {
//
//    /**
//     * 토큰에서 userId 찾기
//     */
//    public static String getUserId(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKeyResolver(SigningKeyResolver.instance)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();  // userId
//    }
//
//    /**
//     * user로 토큰 생성
//     * HEADER : alg, kid
//     * PAYLOAD : sub, iat, exp
//     * SIGNATURE : JwtKey.getRandomKey로 구한 Secret Key로 HS512 해시
//     *
//     * @param member
//     * @return jwt 토큰
//     */
//    public static String createToken(Member member) {
//        log.debug("createToken member={}", member);
//        Claims claims = Jwts.claims().setSubject(member.getId());  // subject
//        Date now = new Date();  // 현재 시간
//        List<Object> key = JwtKeys.getRandomKey();
//
//        // JWT 토큰 생성
//        return Jwts.builder()
//                .setClaims(claims)  // 정보 저장
//                .setIssuedAt(now)  // 토큰 발행 시간 정보
//                .setExpiration(new Date(now.getTime() + 600_000))  // 토큰 만료 시간 설정
//                .setHeaderParam(JwsHeader.KEY_ID, key.get(0))  // kid
//                .signWith((Key) key.get(1))  // signature
//                .compact();
//    }
//}
