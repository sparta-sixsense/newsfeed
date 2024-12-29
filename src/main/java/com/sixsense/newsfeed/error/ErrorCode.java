package com.sixsense.newsfeed.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Base
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 엔티티입니다."),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "이미 존재하는 엔티티입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    USER_INACTIVE_OR_DELETED(HttpStatus.FORBIDDEN, "USER_INACTIVE_OR_DELETED", "삭제되었거나 비활성화된 계정입니다."),
    USER_CONFLICT(HttpStatus.CONFLICT, "USER_CONFLICT", "이미 가입된 이력이 있는 계정입니다."),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "USER_ACCESS_DENIED", "접근 권한이 없습니다."),
    AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILURE", "비밀번호가 일치하지 않습니다."),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_FOUND", "존재하지 않는 토큰입니다"),

    // Follow
    FOLLOWING_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOWING_NOT_FOUND", "존재하는 않는 팔로잉 관계입니다."),
    FOLLOWING_CONFLICT(HttpStatus.CONFLICT, "FOLLOWING_CONFLICT", "이미 팔로잉 상태입니다."),
    ;

    private final HttpStatus status;
    private final String message;
    private final String code;

    ErrorCode(final HttpStatus status, String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
