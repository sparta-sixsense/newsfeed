package com.sixsense.newsfeed.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter // Setter 없으면 에러남. Spring Boot는 @ConfigurationProperties를 사용할 때, 기본적으로 setter를 이용한 프로퍼티 바인딩을 수행
@Getter
@NoArgsConstructor
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String issuer;
    private String secretKey;
}
