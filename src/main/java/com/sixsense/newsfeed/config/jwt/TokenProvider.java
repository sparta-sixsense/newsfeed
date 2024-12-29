package com.sixsense.newsfeed.config.jwt;

import com.sixsense.newsfeed.domain.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

import static com.sixsense.newsfeed.constant.Token.BEARER_PREFIX;

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

    public boolean isValidToken(String rawToken) {
        try {
            String extractedToken = extractToken(rawToken);

            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀키로 복호화
                    .parseClaimsJws(extractedToken);
        } catch (ExpiredJwtException e) { // 디버깅 편의를 위해, 여러개 예외로 분기
            return false;
        } catch (MalformedJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        String extractedToken = extractToken(token);

        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(extractedToken)
                .getBody();
    }

    private String extractToken(String rawToken) { // "Bearer " 접두사 제거
        // Bearer 접두사가 없더라도 우선 허용.
        return rawToken.startsWith(BEARER_PREFIX) ?
                rawToken.substring(BEARER_PREFIX.length()) : rawToken;
    }
}
