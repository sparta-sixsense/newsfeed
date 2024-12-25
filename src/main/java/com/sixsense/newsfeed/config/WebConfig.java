package com.sixsense.newsfeed.config;

import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.service.UserService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;

    @Bean
    public FilterRegistrationBean authorizationFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();

        // filter 등록
        filterFilterRegistrationBean.setFilter(new AuthorizationFilter(tokenProvider));
        // Filter 실행 순서 설정
        filterFilterRegistrationBean.setOrder(1);
        // 전체 URL에 Filter 적용
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }

}
