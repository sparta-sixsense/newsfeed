package com.sixsense.newsfeed.config.jwt;

import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.constant.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration validPeriod) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + validPeriod.toMillis()), user);
    }

    private String makeToken(Date expiration, User user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀키로 복호화
                    .parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isExpiredToken(String token) {
        if (!isValidToken(token)) {
            return false;
        }

        Date now = new Date();
        return getClaims(token)
                .getExpiration()
                .before(now);
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        //String testToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzaXhzZW5zZUBnbWFpbC5jb20iLCJpYXQiOjE3MzUwMzk0NDEsImV4cCI6MTczNTA0NjY0MSwic3ViIjoidGVzdDFAZ21haWwuY29tIiwiaWQiOjF9.KGPwI0Ak0TaXLJjxJCfpTmer8_-0naPatEfB0XWKXSc";
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token) // 토큰값만 들어가야 하는데 bearer 라는 문자열도 같이 들어감
                .getBody();
    }

    // 요청 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Token.AUTHORIZATION_HEADER); // 상수 사용
        if (bearerToken != null && bearerToken.startsWith(Token.BEARER_PREFIX)) {
            return bearerToken.substring(Token.BEARER_PREFIX.length()); // 접두어 제거
        }
        return null;
    }
}

