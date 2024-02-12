package com.baseball.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static String secretKey;

    @Value("${jwt.secret}")
    public void setSecretKey(String key) {
        JwtUtil.secretKey = key;
    }

    // secretKey를 반환하는 메서드 추가
    public static String getSecretKey() {
        return secretKey;
    }

    // 헤더에서 토큰 추출하는 메서드
    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer " 다음의 토큰 값만 추출
        }
        return null;
    }

    // 토큰에서 id 가져오기
    public static Integer getID(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userName", Integer.class);
        } catch (MalformedJwtException e) {
//            log.warn("로깅 - 토큰이 없기때문에 예외가 발생함. 이 예외는 무시해도 좋습니다.");
        }
        return null;
    }

    // 토큰에서 role 가져오기
    public static String getRole(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("role", String.class);
        } catch (MalformedJwtException e) {
//            log.warn("로깅 - 토큰이 없기때문에 예외가 발생함. 이 예외는 무시해도 좋습니다.");
        }
        return null;
    }

    // 토큰이 만료되었는지 확인
    public static boolean isExpired(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration()
                .before(new Date());
    }

    public static String createJwt(Integer userName, Long expireMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }
}
