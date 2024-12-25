package com.sixsense.newsfeed.error.exception.base;


import com.sixsense.newsfeed.error.ErrorCode;

public class ConflictException extends BusinessBaseException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
