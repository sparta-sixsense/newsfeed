package com.sixsense.newsfeed.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Basic
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E1", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E2", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E3", "서버에 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E4", "존재하지 않는 엔티티입니다."),
    CONFLICT(HttpStatus.CONFLICT, "E5", "이미 존재하는 엔티티입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E6", "접근 권한이 없습니다."),

    // User (이건 3개를 하나로 만들어도 될 것 같다. 굳이 이렇게 세분화할 필요가 있는가...)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U4", "존재하지 않는 유저입니다."),
    USER_CONFLICT(HttpStatus.CONFLICT, "U5", "이미 존재하는 유저입니다."),
    AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, "U6", "비밀번호가 일치하지 않습니다."),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "T1", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "T4", "존재하지 않는 토큰입니다"),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P4", "존재하지 않는 게시글입니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "P6", "사용자가 이 게시글에 접근할 권한이 없습니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C4", "존재하지 않는 댓글입니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C6", "해당 댓글에 대한 접근 권한이 없습니다."),

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
