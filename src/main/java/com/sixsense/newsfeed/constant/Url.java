package com.sixsense.newsfeed.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.PatternMatchUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Url {

    public static final String[] WHITE_LIST = {
            "/signup", "/login", "/api/token",
            // Swagger
            "/swagger-ui/*", "/api-docs", "/api-docs/*"
    };

    public static boolean isWhiteList(String requestUrl) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, requestUrl);
    }

}