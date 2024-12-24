package com.sixsense.newsfeed.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(3);
//    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);
public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(2);
}
