package com.sixsense.newsfeed.error.exception;

import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.BusinessBaseException;

public class AuthenticationException extends BusinessBaseException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException() {
        super(ErrorCode.AUTHENTICATION_FAILURE);
    }
}
