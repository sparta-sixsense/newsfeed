package com.sixsense.newsfeed.config.jwt;

import com.sixsense.newsfeed.config.PasswordEncoder;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("generateToken(): 유저 정보와 만료 기한을 전달해 토큰 생성 가능 여부 테스트")
    @Test
    void generateTokenTest() {
        // given
        User testUser = userRepository.save(
                User.builder()
                        .name("홍길동")
                        .email("user@gmail.com")
                        .address("제주특별자치도 ~~")
                        .age(40)
                        .password(passwordEncoder.encode("12345"))
                        .build()
        );

        // when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(testUser.getId()).isEqualTo(userId);
    }

    @DisplayName("isValidToken(): 만료된 토큰 유효성 검증에 실패한다")
    @Test
    void invalidTokenTest() {
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.isExpiredToken(token);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("isValidToken(): 유효한 토큰인 경우 유효성 검증 성공")
    @Test
    void validTokenTest() {
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() + Duration.ofHours(1).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.isExpiredToken(token);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("getUserId(): token으로 userId 가져오기")
    @Test
    void getUserIdTest() {
        // given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when
        Long findUserId = tokenProvider.getUserId(token);

        // then
        assertThat(findUserId).isEqualTo(userId);
    }

    @Test
    void dateAfterBeforeTest() {
        Date expired = new Date(new Date().getTime() - Duration.ofDays(2).toMillis());
        Date valid = new Date(new Date().getTime() + Duration.ofDays(2).toMillis());

        assertThat(expired.before(new Date())).isTrue();
        assertThat(valid.after(new Date())).isTrue();
    }

    @Test
    void test() {
        String encode = passwordEncoder.encode("123");
        System.out.println(encode);
    }
}