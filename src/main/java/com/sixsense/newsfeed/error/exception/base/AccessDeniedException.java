package com.sixsense.newsfeed.error.exception.base;


import com.sixsense.newsfeed.error.ErrorCode;

public class AccessDeniedException extends BusinessBaseException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
